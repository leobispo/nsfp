package com.charite.nsfp.converter;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.exception.ConverterException;
import com.charite.nsfp.dao.NSFPDao;
import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;
import com.charite.nsfp.parser.NSFPParser;
import com.charite.nsfp.parser.NSFPReader;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.ThirdPartyConverter;
import com.charite.thirdpartydb.ThirdPartyConverterFactory;

public class NSFP2SQLJPAConverterFactory implements ThirdPartyConverterFactory {
  @Autowired
  private NSFPDao dao;
  
  @Override
  public ThirdPartyConverter getConverter() {
    return new NSFP2SQLJPAConverter(dao);
  }
}

class NSFP2SQLJPAConverter implements NSFPReader, ThirdPartyConverter {
  private final NSFPDao dao;
  
  public NSFP2SQLJPAConverter(NSFPDao dao) {
    this.dao = dao;
  }

  @Override
  public void setUp() {
  }

  @Override
  public boolean read(Variant variant) {
    dao.save(variant);
    return true;
  }

  @Override
  public boolean read(Gene gene) {
    return true;
  }

  @Override
  public void end() {
  }
  
  @Override
  public void convert(final File file, final ProgressListener progress) {
    NSFPParser parser = new NSFPParser(this);
    
    try {
      parser.parse(file, progress);
    }
    catch (Exception e) {
      throw new ConverterException("Can't convert the NSFP file.", e);
    }
  }

}
