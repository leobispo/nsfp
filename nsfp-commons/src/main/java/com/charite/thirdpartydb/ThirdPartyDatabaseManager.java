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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.charite.download.DownloadFuture;
import com.charite.download.DownloadManager;
import com.charite.download.DownloadResult;
import com.charite.exception.ConverterException;
import com.charite.exception.DownloadException;

/**
 * Responsible to manage all the third party databases (Download, Install, check if it exists, etc.).
 * 
 * @author Leonardo Bispo de Oliveira
 *
 */
@Component
public final class ThirdPartyDatabaseManager {
  
  /** All the attributes will be injected by Spring. */
  
  @Autowired
  private List<ThirdPartyDatabase> databases;

  @Autowired
  @Qualifier(value = "DownloadManager")
  private DownloadManager<ThirdPartyDatabase> manager = null;
  
  /**
   * Return the list of managed third party databases.
   * 
   * @return Managed third party databases.
   * 
   */
  public List<ThirdPartyDatabase> getDatabases() {
    return databases;
  }

  /**
   * Set the list of managed third party databases.
   * 
   * @param databases Managed third party databases.
   * 
   */
  public void setDatabases(List<ThirdPartyDatabase> databases) {
    this.databases = databases;
  }

  /**
   * Verify all databases that need to be installed and, for each not installed database,
   * call the Third Party database to download and install it.
   * 
   * @throws DownloadException
   * 
   */
  public void install() throws DownloadException, ConverterException {
    for (ThirdPartyDatabase db : databases) {
      if (!db.exists())
        db.downloadDatabase();
    }
    if (manager != null) {
      DownloadFuture<ThirdPartyDatabase> future = manager.start();
      List<DownloadResult<ThirdPartyDatabase>> results = future.get();
      if (future.isError())
        throw new DownloadException("Problems to download one or more requested file");
      
      for (DownloadResult<ThirdPartyDatabase> result : results)
        result.data().installDatabase(result.fileName());
    }
    else
      throw new ConverterException("Download Manager cannot be null."); 
  }
}
