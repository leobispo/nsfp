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
package com.charite.thirdpartydb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.charite.download.DownloadManager;
import com.charite.exception.ConverterException;
import com.charite.exception.DownloadException;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.dao.ThirdPartyDatabaseDao;

/**
 * This class will check if an specific Third Party database is already installed. If it is not installed, this class will download the database,
 * extract the database and, for each file matching to a Regex, it will call the ThirdPartyDatabase convert method.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public final class ThirdPartyDatabase implements ProgressListener, UncaughtExceptionHandler {

  /** All the attributes will be injected by Spring. */

  private String fileNameRegex = null;
  private String downloadUrl = null;

  @Autowired
  @Qualifier(value = "DownloadManager")
  private DownloadManager<ThirdPartyDatabase> manager = null;

  @Autowired
  @Qualifier("DownloadListener")
  private ProgressListener listener = null;

  private ThirdPartyConverterFactory converterFactory = null;

  private ThirdPartyDatabaseDao databaseDao = null;

  @Value("${download.cachelocation}")
  private String cacheLocation = null;

  private Lock lock = new ReentrantLock();

  private long startTime = 0;
  private String finalName = "";
  private long allFilesSize = 0;
  private HashMap<String, Long> installFiles = new HashMap<String, Long>();

  private Throwable myException = null;
  private final ExecutorService executor;

  /**
   * Default Constructor.
   *
   */
  public ThirdPartyDatabase() {
    this(1);
  }

  /**
   * Constructor.
   *
   * @param maxInParallel Number of maximum downloads to be executed in parallel per Future.
   *
   *  @throws DownloadException.
   */
  public ThirdPartyDatabase(final int maxInParallel) throws ConverterException {
    if (maxInParallel <= 0 || maxInParallel > 10)
      throw new ConverterException("Invalid maximum parallel downloads (should be between 1 and 10): " + maxInParallel);

    final ThirdPartyDatabase dbThis = this;
    executor = Executors.newFixedThreadPool(maxInParallel, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
        final Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(dbThis);
        return thread;
      }
    });
  }

  /**
   * Set the database download URL.
   *
   * @param downloadUrl Database URL.
   *
   */
  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  /**
   * Set the DAO used to check if the database is already imported.
   *
   * @param databaseDao Database DAO.
   *
   */
  public void setDatabaseDao(ThirdPartyDatabaseDao databaseDao) {
    this.databaseDao = databaseDao;
  }

  /**
   * Set the converter factory that will be used to create converters.
   *
   * @param converterFactory Converter Factory.
   *
   */
  public void setConverterFactory(ThirdPartyConverterFactory converterFactory) {
    this.converterFactory = converterFactory;
  }

  /**
   * Set the regex used to filter all files that will going to be installed.
   *
   * @param fileNameRegex Regular Expression filter.
   *
   */
  public void setFileNameRegex(String fileNameRegex) {
    this.fileNameRegex = fileNameRegex;
  }

  /**
   * This method will check if a Third party database is already imported.
   *
   * @return True if is imported, otherwise false.
   *
   * @throws DownloadException
   *
   */
  public boolean exists() throws DownloadException {
    if (databaseDao == null)
      throw new DownloadException("Cannot execute Exists method. Database not configured");

    return (databaseDao.count() > 0);
  }

  /**
   * This method will download the database from the URL passed via Spring.
   *
   * @throws DownloadException
   *
   */
  public void downloadDatabase() throws DownloadException {
    if (manager == null || listener == null || downloadUrl == null || cacheLocation == null)
      throw new DownloadException("Cannot execute the download. Some configurations are not present");

    try {
      manager.enqueueURL(new URL(downloadUrl), cacheLocation, this, listener);
    }
    catch (MalformedURLException e) {
      throw new DownloadException("Cannot execute the download", e);
    }
  }

  /**
   * Install the database using the ThirdPartyDatabase interface.
   *
   * @param finalFile Downloaded file.
   *
   * @return True if it has been correctly installed, otherwise false.
   */
  public synchronized boolean installDatabase(final String finalFile) {
    File directory = new File(cacheLocation, new StringBuilder().append("downloaddir_").append(UUID.randomUUID()).toString());
    directory.mkdirs();

    myException = null;
    installFiles.clear();
    allFilesSize = 0;
    startTime    = (new Date()).getTime();
    finalName    = finalFile;

    try {
      if (extractCompressedFile(finalFile, directory, false)) {
        listener.start("Extracting: " + finalFile, allFilesSize);
        extractCompressedFile(finalFile, directory, true);
      }
      else
        return false;

    }
    catch (IOException e) {
      return false;
    }
    catch (Exception e) {
      throw new ConverterException("Problems with to extract the database", e);
    }
    finally {
      (new File(finalFile)).delete();
      try {
        delete(directory);
      }
      catch (IOException e) {
        throw new ConverterException("Problems with to extract the database", e);
      }
    }

    if (myException != null) {
      throw new ConverterException("Problems with Executor", myException);
    }

      return true;
  }

  /**
   * Called when a new class with progress behavior is started.
   *
   * @param uid Unique identifier.
   * @param dataSize Total of data to be processed.
   *
   */
  @Override
  public void start(String uid, long dataSize) {
  }

  /**
   * Called when a second is changed or a percent is update.
   *
   * @param uid Unique identifier.
   * @param percent Percents completed.
   * @param seconds Elapsed time in seconds.
   * @param currentDataSize How many information is already processed.
   *
   */
  @Override
  public void progress(String uid, int percent, long seconds, long currentDataSize) {
    try {
      lock.lock();

      installFiles.put(uid, currentDataSize);

      long dataSize = 0;
      for (Long s : installFiles.values())
        dataSize += s;

      long diff = (new Date()).getTime() - startTime;
      seconds   = diff / 1000;
      percent = (int) (((float) dataSize / allFilesSize) * 100);
      listener.progress("Extracting: " + finalName, percent, seconds, dataSize);
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * Called when an error occur.
   *
   * @param uid Unique identifier.
   * @param message Error message.
   */
  @Override
  public void failed(String uid, String message) {
  }

  /**
   * Called when the class finish the process.
   *
   * @param uid Unique identifier.
   *
   */
  @Override
  public void end(String uid) {
    try {
      lock.lock();
      long dataSize = 0;
      for (Long s : installFiles.values())
        dataSize += s;

      if (dataSize == allFilesSize)
        listener.end(finalName);
    }
    finally {
      lock.unlock();
    }
  }

  private static void delete(File file) throws IOException {
    if (file.isDirectory()) {
      for (String f : file.list())
        delete(new File(file, f));
    }

    file.delete();
  }

  private boolean extractCompressedFile(final String file, final File directory, boolean extractFiles) throws IOException {
    InputStream is = null;
    try {
      is = new FileInputStream(new File(file));

      ArchiveInputStream unarchiver = null;
      try {
        if (file.endsWith(".tar.gz")) {
          GZIPInputStream gis;
          gis = new GZIPInputStream(is);

          unarchiver = new TarArchiveInputStream(gis);
        }
        else if (file.endsWith("zip"))
          unarchiver = new ZipArchiveInputStream(is);
        else
          return false;

        ArchiveEntry entry = null;
        while ((entry = unarchiver.getNextEntry()) != null) {
          if (entry.getName().matches(fileNameRegex)) {
            final File outputFile = new File(directory, entry.getName());
            if (extractFiles) {
              final OutputStream outputFileStream = new FileOutputStream(outputFile); 
              IOUtils.copy(unarchiver, outputFileStream);
              outputFileStream.close();
              final ThirdPartyDatabase dbThis = this;

              final ThirdPartyConverter converter = converterFactory.getConverter();
              executor.execute(new Runnable() {
                @Override
                public void run() {
                  converter.convert(outputFile, dbThis);
                }
              });
            }
            else {
              allFilesSize += entry.getSize();
              installFiles.put(outputFile.getAbsolutePath(), 0L);
            }
          }
        }
        
        if (extractFiles) {
          executor.shutdown();
          try {
            while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {}
          }
          catch (InterruptedException e) {}
        }
      }
      finally {
        if (unarchiver != null)
          unarchiver.close();
      }
    }
    finally {
      if (is != null)
        is.close();
    }
    
    return true;
  }

  @Override
  public void uncaughtException(Thread thread, Throwable e) {
    myException = e;
    e.printStackTrace();
  }
}
