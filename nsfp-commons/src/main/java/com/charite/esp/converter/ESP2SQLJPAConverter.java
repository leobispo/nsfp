package com.charite.esp.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.esp.dao.ESPDao;
import com.charite.esp.model.ESP;
import com.charite.esp.parser.ESPParser;
import com.charite.esp.reader.ESPReader;
import com.charite.exception.InvalidFormatException;
import com.charite.thirdpartydb.ThirdPartyConverter;

public class ESP2SQLJPAConverter implements ESPReader, ThirdPartyConverter {
  
  @Autowired
  private ESPDao dao;
  
  @Override
  public void setUp() {
  }
  
  @Override
  public boolean read(ESP esp) {
    dao.save(esp);
    return true;
  }

  @Override
  public void end() {
  }

  @Override
  public void convert(final File file) {
    ESPParser parser = new ESPParser(this);
    
    try {
      parser.parse(file);
    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InvalidFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}