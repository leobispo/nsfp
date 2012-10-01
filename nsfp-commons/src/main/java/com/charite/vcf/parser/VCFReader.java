package com.charite.vcf.parser;

import java.util.List;

import com.charite.snv.model.SNV;

public interface VCFReader {
  void setUp(final String version);
  boolean read(final SNV snv);
  void end(final List<String> header, final List<String> samples);
}
