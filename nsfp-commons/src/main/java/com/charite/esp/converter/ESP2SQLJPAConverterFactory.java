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
package com.charite.esp.converter;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.esp.dao.ESPDao;
import com.charite.esp.model.ESP;
import com.charite.esp.parser.ESPParser;
import com.charite.esp.parser.ESPReader;
import com.charite.exception.ConverterException;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.ThirdPartyConverter;
import com.charite.thirdpartydb.ThirdPartyConverterFactory;

/**
 * This class is responsible to generate ESP2SQLJPAConverters. It will be used by the
 * ThirdPartyDatabase to run multiple converters at the same time.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public class ESP2SQLJPAConverterFactory implements ThirdPartyConverterFactory {
  /** DAO injected by spring. */
  @Autowired
  private ESPDao dao;

  /**
   * Create a new ESP2SQLJPAConverter.
   *
   * @return A new Converter.
   *
   */
  @Override
  public ThirdPartyConverter getConverter() {
    return new ESP2SQLJPAConverter(dao);
  }
}

/**
 * This class will receive a set o ESP entries and store it in the database using JPA.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
class ESP2SQLJPAConverter implements ESPReader, ThirdPartyConverter {
  /** DAO Injected by spring. */
  private final ESPDao dao;

  /**
   * Constructor.
   *
   * @param dao DAO injected by spring.
   *
   */
  public ESP2SQLJPAConverter(final ESPDao dao) {
    this.dao = dao;
  }

  /**
   * Called when the parser is starting.
   *
   */
  @Override
  public void setUp() {
  }

  /**
   * Called for each ESP element parsed.
   *
   * @param esp ESP entry.
   *
   * @return True if the element is correctly consumed, otherwise false (False will abort the parser).
   *
   */
  @Override
  public boolean read(ESP esp) {
    dao.save(esp);
    return true;
  }

  /**
   * Called when the parser is about to finish.
   *
   */
  @Override
  public void end() {
  }

  /**
   * Convert a third party ESP database file to SQL database.
   *
   * @param file Database to be converted.
   * @param progress Progress listener used to show the percent of completed task.
   *
   */
  @Override
  public void convert(final File file, final ProgressListener progress) {
    ESPParser parser = new ESPParser(this);

    try {
      parser.parse(file, progress);
    }
    catch (Exception e) {
      throw new ConverterException("Can't convert the ESP file.", e);
    }
  }
}
