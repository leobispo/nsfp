package com.charite.nsfp.filter;

import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.snv.model.SNV;

public class ThousandGenomeFilter extends Filter<SNV, NSFP> {
  private float threshold = 0;

  @Override
  public String getFilterName() {
    // TODO Auto-generated method stub
    return "NSFP Thousand Genome Filter";
  }

  @Override
  public NSFP filter(SNV in, Object... arguments) {
    // TODO Auto-generated method stub
    return null;
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
    return "NSFP Thousand Genomes Filter: Only show variants with a frequency of less than the threshold passed as parameter";
  }

}
