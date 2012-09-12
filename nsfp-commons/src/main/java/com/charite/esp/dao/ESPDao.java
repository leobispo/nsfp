/* Copyright (C) 2012 Leonardo Bispo de Oliveira and 
 *                    Daniele Sunaga de Oliveira
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.charite.esp.dao;

import java.util.List;

import com.charite.esp.model.ESP;
import com.charite.model.ChromosomeId;

/**
 * ESP Data Access Object.
 * 
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public interface ESPDao {
  
  /**
   * Save a new ESP in the database.
   * 
   * @param esp ESP class to be saved.
   * 
   */
  public void save(final ESP esp);
  
  /**
   * Return all ESP elements stored on the database (use it carefully, because it will fetch all the elements).
   * 
   * @return ESP elements stored on the database.
   * 
   */
  public List<ESP> getAll();
  
  /**
   * Find the ESP stored using the id as a search key.
   *
   * @param id Chromosome ID to be search.
   *
   * @return The ESP element or null.
   *
   */
  public ESP findById(final ChromosomeId id);
}
