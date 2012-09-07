package com.charite.nsfp.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.charite.model.ChromosomeId;

@Entity
@Table(name="variant")
public class Variant {
  @EmbeddedId
  private ChromosomeId id = null;
  
  @Column(name = "aaref")
  private Character aaRef = null;
  
  @Column(name = "aaalt")
  private Character aaAlt = null;
  
  @Column(name = "uniprot_aapos")
  private Integer unitprotAaPos = null;
  
  @Column(name = "aapos")
  private Integer aaPos = null;
  
  private Float sift = null;
  
  private Float polyphen = null;
  
  @Column(name = "mut_taster")
  private Float mutTaster = null;
  
  private Float phyloP = null;
  
  @Column(name = "ThGenomes_AC")
  private Short thGenomesAC = null;
  
  @Column(name = "ThGenomes_AF")
  private Float thGenomesAF = null;

  @ManyToOne(optional=false)
  private Gene gene;

  public ChromosomeId getId() {
    return id;
  }

  public void setId(ChromosomeId id) {
    this.id = id;
  }

  public Character getAaRef() {
    return aaRef;
  }

  public void setAaRef(Character aaRef) {
    this.aaRef = aaRef;
  }

  public Character getAaAlt() {
    return aaAlt;
  }

  public void setAaAlt(Character aaAlt) {
    this.aaAlt = aaAlt;
  }

  public Integer getUnitprotAaPos() {
    return unitprotAaPos;
  }

  public void setUnitprotAaPos(Integer unitprotAaPos) {
    this.unitprotAaPos = unitprotAaPos;
  }

  public Integer getAaPos() {
    return aaPos;
  }

  public void setAaPos(Integer aaPos) {
    this.aaPos = aaPos;
  }

  public Float getSift() {
    return sift;
  }

  public void setSift(Float sift) {
    this.sift = sift;
  }

  public Float getPolyphen() {
    return polyphen;
  }

  public void setPolyphen(Float polyphen) {
    this.polyphen = polyphen;
  }

  public Float getMutTaster() {
    return mutTaster;
  }

  public void setMutTaster(Float mutTaster) {
    this.mutTaster = mutTaster;
  }

  public Float getPhyloP() {
    return phyloP;
  }

  public void setPhyloP(Float phyloP) {
    this.phyloP = phyloP;
  }

  public Short getThGenomesAC() {
    return thGenomesAC;
  }

  public void setThGenomesAC(Short thGenomesAC) {
    this.thGenomesAC = thGenomesAC;
  }

  public Float getThGenomesAF() {
    return thGenomesAF;
  }

  public void setThGenomesAF(Float thGenomesAF) {
    this.thGenomesAF = thGenomesAF;
  }

  public Gene getGene() {
    return gene;
  }

  public void setGene(Gene gene) {
    this.gene = gene;
  }

  //TODO: Getter Setter, To String, Hash, Equals!!!
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("[+] Variant ==================================================\n")
           .append(id.toString());
    
    return builder.toString();
  }
  
  @Override
  public int hashCode() {
    int hash = 0;
    hash = 31 * hash + hash(id);

    return hash;    
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof Variant) {
      Variant c = (Variant) o;
      return (equal(id, c.id));
    }
    
    return false;
  }
  
  private static int hash(Object o) {
    return o == null ? 0 : o.hashCode();
  }
  
  private static boolean equal(Object o, Object another)
  {
    return o == null ? another == null : o.equals(another);
  }
}