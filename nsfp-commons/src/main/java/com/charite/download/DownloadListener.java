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

import java.net.URL;

/**
 * This interface will be responsible to manage the state of one or more downloads. Whenever a new download is started, 
 * the start method will be called. For each second or percent update, the progress method will be called. If for some 
 * reason the download is failed, the failed method will be called with the error message.
 * 
 * @author Leonardo Bispo de Oliveira
 *
 */
public interface DownloadListener {
  /**
   * Called when a new download is started.
   * 
   * @param url Download URL.
   * @param fileSize Size of the file to download.
   * 
   */
  void start(final URL url, final long fileSize);
  
  /**
   * Called when a second is changed or a percent is update.
   * 
   * @param url Download URL.
   * @param percent Percents completed.
   * @param seconds Elapsed time in seconds.
   * 
   */
  void progress(final URL url, final int percent, final long seconds);
  
  /**
   * Called when a download error occur.
   * 
   * @param message Error message.
   */
  void failed(final String message);
}
