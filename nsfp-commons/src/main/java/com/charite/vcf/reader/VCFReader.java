package com.charite.vcf.reader;

import java.util.List;

import com.charite.snv.model.SNV;

public interface VCFReader {
  public void setUp(final String version);
  public boolean read(final SNV vcf);
  public void end(final List<String> header);
}
