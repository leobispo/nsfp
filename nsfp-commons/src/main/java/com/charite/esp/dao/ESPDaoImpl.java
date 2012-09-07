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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.charite.esp.model.ESP;
import com.charite.model.ChromosomeId;
import com.charite.thirdpartydb.dao.ThirdPartyDatabaseDao;

/**
 * ESP Data Access Object JPA implementation.
 * 
 * @author Leonardo Bispo de Oliveira
 *
 */
public class ESPDaoImpl implements ThirdPartyDatabaseDao, ESPDao {
  
  private EntityManager entityManager;

  /**
   * Return the number of rows in the third party database.
   * 
   * @return Number of rows.
   * 
   */
  @Override
  public Long count() {
    return (Long) entityManager.createQuery("SELECT COUNT(esp) FROM ESP esp").getSingleResult();
  }

  /**
   * Save a new ESP in the database.
   * 
   * @param esp ESP class to be saved.
   * 
   */
  @Override
  @Transactional
  public void save(ESP esp) {
   entityManager.persist(esp);
  }
  
  /**
   * Return the number of rows in the third party database.
   * 
   * @return Number of rows.
   * 
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<ESP> getAll() {
    return (List<ESP>) entityManager.createQuery("SELECT esp FROM ESP esp").getResultList();
  }
  
  @Override
  public ESP findById(final ChromosomeId id) {
    return entityManager.find(ESP.class, id);
  }
  
  /**
   * Used to inject the Entity Manager via Spring.
   * 
   * @param entityManager
   */
  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
}
