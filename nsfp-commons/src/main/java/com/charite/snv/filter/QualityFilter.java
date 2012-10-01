package com.charite.snv.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.enums.Genotype;
import com.charite.esp.dao.ESPDao;
import com.charite.filter.Filter;
import com.charite.model.ChromosomeId;
import com.charite.snv.model.SNV;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("SNVFilter")
public final class QualityFilter extends Filter<SNV, SNV> {
  private Integer threshold = 0;
  
  @Autowired
  private ESPDao dao;
  
  @Override
  public String getFilterName() {
    return "Quality Filter";
  }

  @Override
  public SNV filter(final SNV snv, Object... arguments) {
    if (snv.getESP() == null)
      snv.setESP(dao.findById(new ChromosomeId(snv.getChromosome(), snv.getPosition(), snv.getRef().charAt(0), snv.getAlt().charAt(0))));
    
    if (snv.getGenotype() == Genotype.GENOTYPE_HOMOZYGOUS_REF)
      return snv;

    if (snv.getGenotypeQuality() != null && snv.getGenotypeQuality() < threshold)
      return snv;
    
    return null;
  }

  @Override
  public boolean setParameter(final String parameter) {
    try {
      threshold = Integer.parseInt(parameter);
    }
    catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  @Override
  public String getDescription() {
    return "Genotype quality filter. Do not show variants with quality less than the threshold passed as parameter";
  }
}
