package com.charite.nsfp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.charite.model.ChromosomeId;
import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;
import com.charite.thirdpartydb.dao.ThirdPartyDatabaseDao;

public class NSFPDaoImpl implements ThirdPartyDatabaseDao, NSFPDao {
  
  private EntityManager entityManager;
  
  @Override
  public Variant findById(ChromosomeId id) {
    return entityManager.find(Variant.class, id);
  }

  @Override
  @Transactional
  public void save(Variant variant) {
    entityManager.persist(variant.getGene());
    entityManager.persist(variant);
  }

  @Override
  @Transactional
  public void save(Gene gene) {
    entityManager.persist(gene);
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
