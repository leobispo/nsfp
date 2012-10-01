/* Copyright (C) 2012 Leonardo Bispo de Oliveira and 
 *                    Daniele Sunaga de Oliveira
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.charite.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.charite.exception.DownloadException;
import com.charite.progress.ProgressListener;

/**
 * Manage a set of file downloads. This class implements a Future concept, where non blocking tasks are enqueued
 * and executed in parallel, keeping the main thread unlocked. You will lock the main thread only if you call
 * the DownloadFuture get() method.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public final class DownloadManager<T> {

  private Semaphore sem = new Semaphore(0);
  /** Number of maximum downloads to be executed in parallel per Future. */
  private final int maxInParallel;
  /** Current future. Must the swap of a future must be an atomic operation. */
  private final AtomicReference<DownloadFutureImpl> future;

  /**
   * Default Constructor.
   *
   * @throws DownloadException.
   *
   */
  public DownloadManager() throws DownloadException {
    this(1);
  }

  /**
   * Constructor.
   *
   * @param maxInParallel Number of maximum downloads to be executed in parallel per Future.
   *
   *  @throws DownloadException.
   */
  public DownloadManager(final int maxInParallel) throws DownloadException {
    if (maxInParallel <= 0 || maxInParallel > 10)
      throw new DownloadException("Invalid maximum parallel downloads (should be between 1 and 10): " + maxInParallel);

    this.maxInParallel = maxInParallel;
    future = new AtomicReference<>(new DownloadFutureImpl(maxInParallel));
  }

  /**
   * Enqueue a new URL file to download. This operation will not start the download. You must call the start() method to
   * start all enqueued files.
   *
   * @param url URL file to download.
   * @param downloadPath Place where the file will be stored.
   * @param data A generic information to be retrieved by this class.
   * @param listener The Download listener implementation.
   */
  public void enqueueURL(final URL url, final String downloadPath, final T data, final ProgressListener listener) {
    future.get().listeners.add(new DownloadElement(url, downloadPath, listener, data));
  }

  /**
   *
   * @return
   */
  public DownloadFuture<T> start() {
    future.get().start();
    return future.getAndSet(new DownloadFutureImpl(maxInParallel));
  }

  // After this point you will find the INNER classes implementation.

  private final class DownloadFutureImpl extends Thread implements DownloadFuture<T> {
    private boolean error                           = false;
    private boolean done                            = false;
    private final List<DownloadResult<T>> listeners = new ArrayList<>();
    private final ExecutorService executor;

    public DownloadFutureImpl(final int maxInParallel) {
      executor = Executors.newFixedThreadPool(maxInParallel);
    }

    @Override
    public List<DownloadResult<T>> get() {
      try {
        if (!done)
          sem.acquire();
      }
      catch (InterruptedException e) {
        throw new DownloadException("Probems in get() method", e);
      }

      return listeners;
    }

    @Override
    public boolean isDone() {
      return done;
    }

    @Override
    public boolean isError() {
      return error;
    }

    @Override
    public void run() {
      for (DownloadResult<T> element : listeners) {
        DownloadElement downloadElement = (DownloadElement) element;
        downloadElement.setParent(this);
        executor.submit(downloadElement);
      }

      executor.shutdown();
      try {
        while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {}
      }
      catch (InterruptedException e) {
        throw new DownloadException("Problems to wait workers to finshish their tasks", e);
      }

      sem.release();
      done = true;
    }

    public void setError(final boolean error) {
      this.error = error;
    }
  }

  private final class DownloadElement implements DownloadResult<T>, Runnable {
    private boolean error             = false;
    private  String fileName          = "";
    private DownloadFutureImpl parent = null;

    private final URL url;
    private final T data;
    private final String downloadPath;
    private final ProgressListener listener;

    public DownloadElement(final URL url, final String downloadPath, final ProgressListener listener, final T data) {
      this.url          = url;
      this.downloadPath = downloadPath;
      this.listener     = listener;
      this.data         = data;
    }

    public void setParent(final DownloadFutureImpl parent) {
      this.parent = parent;
    }

    @Override
    public boolean isError() {
      return error;
    }

    @Override
    public String fileName() {
      return fileName;
    }

    @Override
    public T data() {
      return data;
    }

    @Override
    public void run() {
      File file = null;
      URLConnection connection;
      try {
        connection = url.openConnection();

        connection.connect();
        String type = connection.getContentType();
        if (type != null) {
          long length  = connection.getContentLength();
          String name = connection.getHeaderField("Content-Disposition");

          if (length < 0)
            length = Long.parseLong(connection.getHeaderField("Content-Length"));

          if (name == null)
            name = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);

          file = getFile(downloadPath, name);

          if (file.exists())
            file.delete();

          fileName = file.getAbsolutePath();
          try (FileOutputStream fos = new FileOutputStream(file);
              InputStream is        = connection.getInputStream()) {

            final byte[] buffer = new byte[8192];
            int read = 0;

            long stime = (new Date()).getTime();
            long seconds = 0;
            int percent  = -1;

            float readLength = 0;

            if (listener != null)
              listener.start(url.toString(), length);
            while ((read = is.read(buffer)) > 0) {
              fos.write(buffer, 0, read);
              readLength += read;
              int newPercent = (int) ((readLength / length) * 100);

              long diff = (new Date()).getTime() - stime;
              long elapsedSeconds = diff / 1000;

              if (percent != newPercent || seconds < elapsedSeconds) {
                seconds = elapsedSeconds;
                percent = newPercent;
                if (listener != null)
                  listener.progress(url.toString(), percent, seconds, (long) readLength);
              }
            }

            if (listener != null)
              listener.end(url.toString());
          }
        }
      } catch (IOException e) {
        if (listener != null)
          listener.failed(url.toString(), e.getMessage());
        parent.setError(true);
        error = true;
        if (file != null)
          file.delete();
      }
    }

    private File getFile(final String downloadParh, final String name) {
      if (name != "")
        return new File(downloadParh, name);

      return new File(downloadParh, new StringBuilder().append("download_").append(UUID.randomUUID()).toString());
    }
  }
}
