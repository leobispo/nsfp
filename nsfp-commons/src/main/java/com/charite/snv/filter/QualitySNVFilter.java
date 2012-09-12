package com.charite.snv.filter;

import com.charite.enums.Genotype;
import com.charite.snv.model.SNV;

public class QualitySNVFilter implements SNVFilter {
  //TODO: RECORD THE NUMBER OF FILTERED ELEMENTS!!
  private int qualityThreshold = 0;

  @Override
  public String filterName() {
    return null;
  }

  @Override
  public String filterDescription() {
    return null;
  }

  @Override
  public boolean hasArgs() {
    return false;
  }

  boolean filter(SNV snv) {
    if (snv.getGenotype() == Genotype.GENOTYPE_HOMOZYGOUS_REF)
      return true;

    if (snv.getGenotypeQuality() != null && snv.getGenotypeQuality() < qualityThreshold)
      return true;

    return false;
  }
}
