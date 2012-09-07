package com.charite.snv.filter;

import com.charite.snv.model.SNV;

public class ThousandGenomesSNVFilter implements SNVFilter {
  private int thousandGenomesThreshold = 0;
  
  @Override
  public String filterName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String filterDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasArgs() {
    // TODO Auto-generated method stub
    return false;
  }

  boolean filter(SNV snv) {
    if (snv.getESP() == null)
      return true;
    
    if (snv.getESP().getFrequency() < -0.5)
      return true;
    
    if (snv.getESP().getFrequency() > thousandGenomesThreshold)
      return true;
    
    return false;
  }
}
