package com.charite.nsfp.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.charite.esp.dao.ESPDao;
import com.charite.filter.Filter;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.dao.NSFPDao;
import com.charite.nsfp.model.NSFP;
import com.charite.nsfp.model.Variant;
import com.charite.snv.model.SNV;

public class ThousandGenomeFilter extends Filter<SNV, NSFP> {
  private float threshold = 0;

  @Autowired
  private ESPDao dao;

  @Autowired
  private NSFPDao nDao;
  
  @Override
  public String getFilterName() {
    return "NSFP Thousand Genome Filter";
  }

  @Override
  public NSFP filter(SNV snv, Object... arguments) {
    if (snv.getESP() == null)
      snv.setESP(dao.findById(new ChromosomeId(snv.getChromosome(), snv.getPosition(), snv.getRef().charAt(0), snv.getAlt().charAt(0))));

    if (!(snv.getESP().getFrequency() > -0.5 && snv.getESP().getFrequency() <= threshold))
      return null;

    final Variant variant = nDao.findById(new ChromosomeId(snv.getChromosome(), snv.getPosition(), snv.getRef().charAt(0), snv.getAlt().charAt(0)));
    if (variant != null) {
      NSFP nsfp = new NSFP(snv, variant);
      if (nsfp.getThGenomesAF() != null && nsfp.getThGenomesAF() <= threshold)
        return nsfp;
    }
    
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
