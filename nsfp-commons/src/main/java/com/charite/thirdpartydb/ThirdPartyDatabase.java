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
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.AbstractZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.charite.download.DownloadManager;
import com.charite.exception.DownloadException;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.dao.ThirdPartyDatabaseDao;

/**
 * This class will check if an specific Third Party database is already installed. If it is not installed, this class will download the database,
 * extract the database and, for each file matching to a Regex, it will call the ThirdPartyDatabase convert method.
 * 
 * @author Leonardo Bispo de Oliveira
 * 
 */
public final class ThirdPartyDatabase {
  
  /** All the attributes will be injected by Spring. */
  
  private String fileNameRegex = null;
  private String downloadUrl = null;
  
  @Autowired
  @Qualifier(value = "DownloadManager")
  private DownloadManager<ThirdPartyDatabase> manager = null;
  
  @Autowired
  private ProgressListener listener = null;
  
  private ThirdPartyConverter converter = null;
  
  private ThirdPartyDatabaseDao databaseDao = null;
  
  @Value("${download.cachelocation}")
  private String cacheLocation = null;
  
  /**
   * Return the database download URL.
   * 
   * @return Database URL.
   * 
   */
  public String getDownloadUrl() {
    return downloadUrl;
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
   * Return the DAO used to check if the database is already imported.
   * 
   * @return Database DAO.
   * 
   */
  public ThirdPartyDatabaseDao getDatabaseDao() {
    return databaseDao;
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
   * Return the regex used to filter all files that will going to be installed.
   * 
   * @return Regular Expression filter.
   * 
   */
  public String getFileNameRegex() {
    return fileNameRegex;
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
   * Return the converter used to convert the Database to other database format.
   * 
   * @return Third party converter implementation.
   */
  public ThirdPartyConverter getConverter() {
    return converter;
  }

  /**
   * Set the converter used to convert the Database to other database format.
   * 
   * @param converter Third party converter implementation.
   */
  public void setConverter(ThirdPartyConverter converter) {
    this.converter = converter;
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
  public boolean installDatabase(final String finalFile) {
    File directory = new File(cacheLocation, new StringBuilder().append("downloaddir_").append(UUID.randomUUID()).toString());
    directory.mkdirs();

    if (extractCompressedFile(finalFile, directory)) {
      for (File f : listFilesMatching(directory, fileNameRegex))
        converter.convert(f);
      try {
        (new File(finalFile)).delete();
        delete(directory);
      }
      catch (IOException e) {
        return false;
      }
    }

    return true;
  }
  
  private static File[] listFilesMatching(File root, String regex) {
    if(!root.isDirectory())
      throw new IllegalArgumentException(root + " is not a directory.");

    final Pattern p = Pattern.compile(regex);
    return root.listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return p.matcher(pathname.getName()).matches();
      }
    });
  }

  private static void delete(File file) throws IOException {
    if (file.isDirectory()) {
      for (String f : file.list())
        delete(new File(file, f));
    }

    file.delete();
  }

  private static boolean extractCompressedFile(final String file, final File directory) {
    AbstractZipUnArchiver unarchiver;
    if (file.endsWith(".tar.gz"))
      unarchiver = new TarGZipUnArchiver();
    else if (file.endsWith("zip"))
      unarchiver = new ZipUnArchiver();
    else
      return false;

    unarchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DISABLED, "none"));
    unarchiver.setSourceFile(new File(file));
    unarchiver.setDestDirectory(directory);
    unarchiver.extract();

    return true;
  }
}
