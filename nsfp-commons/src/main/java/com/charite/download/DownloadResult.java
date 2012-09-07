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

/**
 * This interface is responsible for returning the result of a download. You can check the file name, if an error occurred and
 * receive a data specific object passed to the DownloadManager enqueueFile method.
 * 
 * @author Leonard Bispo de Oliveira
 *
 */
public interface DownloadResult<T> {
  /**
   * The absolute path of the file.
   * 
   * @return The absolute path of the file.
   */
  public String fileName();
  
  /**
   * Return if an error occurred while downloading the file.
   * 
   * @return True if error, otherwise file.
   * 
   */
  public boolean isError();
  
  /**
   * User specific data passed to the DownloadManager enqueueFile method.
   * 
   * @return User specific data.
   */
  public T data();
}
