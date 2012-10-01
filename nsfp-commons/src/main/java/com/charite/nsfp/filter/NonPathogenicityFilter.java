package com.charite.nsfp.filter;

import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.snv.model.SNV;

public class NonPathogenicityFilter extends Filter<SNV, NSFP> {

  @Override
  public String getFilterName() {
    return "Non Pathogenicity Filter";
  }

  @Override
  public NSFP filter(SNV snv, Object... arguments) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setParameter(String parameter) {
    return false;
  }

  @Override
  public String getDescription() {
    return null;
  }

}
