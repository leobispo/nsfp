package com.charite.nsfp.filter.inheritance;

import com.charite.enums.Genotype;
import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.util.Pair;

public class AutosomalDominantFilter extends Filter<NSFP, Pair<Boolean, Boolean>> {

  @Override
  public String getFilterName() {
    return "Autosomal Dominant Filter";
  }

  @Override
  public String getDescription() {
    return "Autosomal dominant filter (>=1heterozygous mutations in a gene)";
  }

  @Override
  public Pair<Boolean, Boolean> filter(NSFP nsfp, Object... arguments) {  
    if (nsfp.getSnv().getGenotype() == Genotype.GENOTYPE_HETEROZYGOUS)
      return new Pair<Boolean, Boolean>(true, true);
    
    return new Pair<>(false, true);
  }

  @Override
  public boolean setParameter(String parameter) {
    return true;
  }
}
