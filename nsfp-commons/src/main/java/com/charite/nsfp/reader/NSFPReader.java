package com.charite.nsfp.reader;

import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;

public interface NSFPReader {
  
  /**
   * Called when the parser is starting.
   * 
   */
  public void setUp();
  
  public boolean read(final Variant variant);
  public boolean read(final Gene gene);
  
  /**
   * Called when the parser is about to finish.
   * 
   */
  public void end();
}
