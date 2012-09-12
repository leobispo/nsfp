package com.charite.snv.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.esp.dao.ESPDao;
import com.charite.exception.ConverterException;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.dao.NSFPDao;
import com.charite.progress.ProgressListener;
import com.charite.snv.model.SNV;
import com.charite.vcf.parser.VCFParser;
import com.charite.vcf.reader.VCFReader;

public final class VCF2SNVConverter implements VCFReader {
  private String version       = null;
  private List<String> header  = null;
  private final List<SNV> snvs = new ArrayList<SNV>();
  private Lock lock            = new ReentrantLock();

  @Autowired
  private ProgressListener progressListener;

  @Autowired
  private ESPDao espDao;

  @Autowired
  private NSFPDao nsfpDao;

  @Override
  public void setUp(String version) {
    this.version = version;
  }

  @Override
  public boolean read(SNV snv) {
    lock.lock();
    try {
      if (snv.getChromosome() == null || snv.getPosition() == null || snv.getRef().length() == 0 || snv.getAlt().length() == 0)
        return true;

      ChromosomeId id = new ChromosomeId(snv.getChromosome(), snv.getPosition(), snv.getRef().charAt(0), snv.getAlt().charAt(0));
      snv.setESP(espDao.findById(id));
      snv.setVariant(nsfpDao.findById(id)); // TODO: This must be inside the filter. I will save lots of SQL calls.
    }
    finally {
      lock.unlock();
    }
    return true;
  }

  @Override
  public void end(List<String> header) {
    lock.lock();
    try {
      this.header = header;
    }
    finally {
      lock.unlock();
    }
  }

  public List<SNV> convert(final File vcfFile) {
    lock.lock();
    try {
      this.version = null;
      this.header  = null;
      this.snvs.clear();
    }
    finally {
      lock.unlock();
    }

    VCFParser parser = new VCFParser(this);

    try {
      parser.parse(vcfFile, progressListener);
    }
    catch (Exception e) {
      throw new ConverterException("Can't convert the ESP file.", e);
    }

    return snvs;
  }

  public String getVersion() {
    lock.lock();
    try {
      return version;
    }
    finally {
      lock.unlock();
    }
  }

  public List<String> getHeader() {
    lock.lock();
    try {
      return header;
    }
    finally {
      lock.unlock();
    }
  }
}
