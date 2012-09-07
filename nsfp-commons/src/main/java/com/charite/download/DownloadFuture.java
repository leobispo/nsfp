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

import java.util.List;

/**
 * This interface is responsible to return the result from a set of enqueued download tasks.
 * This is a Future class, all download operation is asynchronous.
 * 
 * @author Leonardo Bispo de Oliveira
 *
 */
public interface DownloadFuture<T> {
  /**
   * This method will lock the execution until the manager finish the download of all enqueued files and return a set of
   * download results.
   * 
   * @return List of download results.
   * 
   */
  public List<DownloadResult<T>> get();
  
  /**
   * This method will ask if all downloads are completed. This operation will not lock the thread.
   * 
   * @return True if the operation is done, otherwise false.
   * 
   */
  public boolean isDone();
  
  /**
   * This method will return if an error occurred in the download method.
   * 
   * @return True if an error occurred, otherwise false.
   * 
   */
  public boolean isError();
}
