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
package com.charite.progress;

/**
 * This interface will be responsible to manage the progress of several data. Whenever a new class has a progress started,
 * the start method will be called. For each second or percent update, the progress method will be called. If for some
 * reason the download failed, the failed method will be called with the error message. If the class finished the the progress,
 * the end method will be called.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public interface ProgressListener {
  /**
   * Called when a new class with progress behavior is started.
   *
   * @param uid Unique identifier.
   * @param dataSize Total of data to be processed.
   *
   */
  void start(final String uid, final long dataSize);

  /**
   * Called when a second is changed or a percent is update.
   *
   * @param uid Unique identifier.
   * @param percent Percents completed.
   * @param seconds Elapsed time in seconds.
   * @param currentDataSize How many information is already processed.
   *
   */
  void progress(final String uid, final int percent, final long seconds, final long currentDataSize);

  /**
   * Called when an error occur.
   *
   * @param uid Unique identifier.
   * @param message Error message.
   * 
   */
  void failed(final String uid, final String message);

  /**
   * Called when the class finish the process.
   *
   * @param uid Unique identifier.
   *
   */
  void end(final String uid);
}
