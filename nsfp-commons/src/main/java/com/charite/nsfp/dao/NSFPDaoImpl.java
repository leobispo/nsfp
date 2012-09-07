package com.charite.nsfp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.charite.esp.model.ESP;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.model.Variant;
import com.charite.thirdpartydb.dao.ThirdPartyDatabaseDao;

public class NSFPDaoImpl implements ThirdPartyDatabaseDao, NSFPDao {
  
  private EntityManager entityManager;
  
  @Override
  public Variant findById(ChromosomeId id) {
    return entityManager.find(Variant.class, id);
  }

  @Override
  public void save(ESP esp) {
    entityManager.persist(esp);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Variant> getAll() {
    return (List<Variant>) entityManager.createQuery("SELECT variant FROM Variant variant").getResultList();
  }

  @Override
  public Long count() {
    return (Long) entityManager.createQuery("SELECT COUNT(variant) FROM Variant variant").getSingleResult();
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
