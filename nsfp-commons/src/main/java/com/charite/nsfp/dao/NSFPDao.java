package com.charite.nsfp.dao;

import java.util.List;

import com.charite.esp.model.ESP;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.model.Variant;

public interface NSFPDao {

  /**
   * Save a new Variant in the database.
   * 
   * @param variant Variant class to be saved.
   * 
   */
  public void save(final ESP esp);
  
  /**
   * Return all Variant elements stored on the database (use it carefully, because it will fetch all the elements).
   * 
   * @return Variant elements stored on the database.
   * 
   */
  public List<Variant> getAll();
  
  public Variant findById(final ChromosomeId id);
}
