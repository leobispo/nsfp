package com.charite.nsfp.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.filter.Filter;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.dao.NSFPDao;
import com.charite.nsfp.model.NSFP;
import com.charite.nsfp.model.Variant;
import com.charite.snv.model.SNV;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("Filter")
public class PathogenicityFilter extends Filter<SNV, NSFP> {

  @XStreamOmitField
  @Autowired
  private NSFPDao dao;
  
  @Override
  public String getFilterName() {
    return "Pathogenicity Filter";
  }

  @Override
  public NSFP filter(final SNV snv, Object... arguments) {
    if (!snv.isNonPathogenic())
    {
      final Variant variant = dao.findById(new ChromosomeId(snv.getChromosome(), snv.getPosition(), snv.getRef().charAt(0), snv.getAlt().charAt(0)));
      if (variant != null) {
        final NSFP nsfp = new NSFP(snv, variant);
        if (nsfp.isPredictedPathogenic())
          return nsfp;
      }

      return new NSFP(snv);
    }
    
    return new NSFP(snv); //TODO: JUST FOR TESTS!!! RETURN NULL!!
  }

  @Override
  public boolean setParameter(final String parameter) {
    return false;
  }

  @Override
  public String getDescription() {
    return "Pathogenicity: At least one of SIFT, Polyphen2 (HVAR), or Mutation Taster predicts damaging effect";
  }

}
