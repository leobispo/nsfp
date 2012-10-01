package com.charite.snv.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.esp.dao.ESPDao;
import com.charite.filter.Filter;
import com.charite.model.ChromosomeId;
import com.charite.snv.model.SNV;

public class ThousandGenomeFilter extends Filter<SNV, SNV> {
  private float threshold = 0;
  @Autowired
  private ESPDao dao;
  
  @Override
  public String getFilterName() {
    return "SNV Thousand Genomes Filter";
    
  }

  @Override
  public SNV filter(final SNV snv, Object... arguments) {
    if (snv.getESP() == null)
      snv.setESP(dao.findById(new ChromosomeId(snv.getChromosome(), snv.getPosition(), snv.getRef().charAt(0), snv.getAlt().charAt(0))));
    return snv;
  }

  @Override
  public boolean setParameter(String parameter) {
    try {
      threshold = Float.parseFloat(parameter);
    }
    catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  @Override
  public String getDescription() {
    return "SNV Thousand Genomes Filter: Only show variants with a frequency of less than the threshold passed as parameter";
  }
}
