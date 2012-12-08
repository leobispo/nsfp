package com.charite.nsfp.filter.inheritance;

import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.util.Pair;

public class XChromosomalFilter extends Filter<NSFP, Pair<Boolean, Boolean>> {

  @Override
  public String getFilterName() {
    return "X Dominant Filter";
  }

  @Override
  public String getDescription() {
    return "Chromosomal filter (any mutations in a gene on X chromosome)";
  }

  @Override
  public Pair<Boolean, Boolean> filter(NSFP nsfp, Object... arguments) {
    if (nsfp.getChromosome() == 23)
      return new Pair<Boolean, Boolean>(true, true);
    
    return new Pair<>(false, false);
  }

  @Override
  public boolean setParameter(String parameter) {
    return true;
  }
}
