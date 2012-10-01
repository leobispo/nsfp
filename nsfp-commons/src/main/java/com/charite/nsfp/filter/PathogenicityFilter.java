package com.charite.nsfp.filter;

import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.snv.model.SNV;

public class PathogenicityFilter extends Filter<SNV, NSFP> {

  @Override
  public String getFilterName() {
    return "Pathogenicity Filter";
  }

  @Override
  public NSFP filter(SNV in, Object... arguments) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setParameter(String parameter) {
    return false;
  }

  @Override
  public String getDescription() {
    return "Pathogenicity: At least one of SIFT, Polyphen2 (HVAR), or Mutation Taster predicts damaging effect";
  }

}
