package com.charite.nsfp.filter.inheritance;

import com.charite.enums.Genotype;
import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.util.Pair;

public class AutosomalRecessiveFilter extends Filter<NSFP, Pair<Boolean, Boolean>> {

  @Override
  public String getFilterName() {
    return "Autosomal Recessive Filter";
  }

  @Override
  public String getDescription() {
    return "Autosomal recessive filter (homozygous or compound heterozygous mutations in a gene)";
  }

  @Override
  public Pair<Boolean, Boolean> filter(NSFP nsfp, Object... arguments) { 
    Integer counter = 0;
    if (arguments.length > 0)
      counter = (Integer) arguments[0];
    
    if (counter > 1 || nsfp.getSnv().getGenotype() == Genotype.GENOTYPE_HOMOZYGOUS_ALT)
      return new Pair<Boolean, Boolean>(true, true);
    
    return new Pair<>(false, true);
  }

  @Override
  public boolean setParameter(String parameter) {
    return true;
  }
}
