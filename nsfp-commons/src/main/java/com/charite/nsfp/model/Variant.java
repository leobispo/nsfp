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
  private Gene gene = null;

  public Variant() {
  }
  
  public Variant(final ChromosomeId id, final Character aaRef, final Character aaAlt, final Integer unitprotAaPos,
    final Integer aaPos, final Float sift, final Float polyphen, final Float mutTaster,
    final Float phyloP, final Short thGenomesAC, final Float thGenomesAF, final Gene gene) {
    this.id            = id;
    this.aaRef         = aaRef;
    this.aaAlt         = aaAlt;
    this.unitprotAaPos = unitprotAaPos;
    this.aaPos         = aaPos;
    this.sift          = sift;
    this.polyphen      = polyphen;
    this.mutTaster     = mutTaster;
    this.phyloP        = phyloP;
    this.thGenomesAC   = thGenomesAC;
    this.thGenomesAF   = thGenomesAF;
    this.gene          = gene;
  }
  
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
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("[+] Variant ==================================================\n")
           .append(id)
           .append("AA Ref             : ").append(aaRef).append("\n")
           .append("AA Alt             : ").append(aaAlt).append("\n")
           .append("Uni. Prot. AA Pos. : ").append(unitprotAaPos).append("\n")
           .append("AA Pos             : ").append(aaPos).append("\n")
           .append("Sift               : ").append(sift).append("\n")
           .append("Polyphen           : ").append(polyphen).append("\n")
           .append("Mut. Taster        : ").append(mutTaster).append("\n")
           .append("Phylo P            : ").append(phyloP).append("\n")
           .append("TH Genmoes AC      : ").append(thGenomesAC).append("\n")
           .append("TH Genomes AF      : ").append(thGenomesAF).append("\n")
           .append(gene).append("\n");

    return builder.toString();
  }
  
  @Override
  public int hashCode() {
    int hash = 0;
    hash = 31 * hash + hash(id);
    hash = 31 * hash + hash(aaRef);
    hash = 31 * hash + hash(aaAlt);
    hash = 31 * hash + hash(unitprotAaPos);
    hash = 31 * hash + hash(aaPos);
    hash = 31 * hash + hash(sift);
    hash = 31 * hash + hash(polyphen);
    hash = 31 * hash + hash(mutTaster);
    hash = 31 * hash + hash(phyloP);
    hash = 31 * hash + hash(thGenomesAC);
    hash = 31 * hash + hash(thGenomesAF);
    hash = 31 * hash + hash(gene);

    return hash;    
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof Variant) {
      Variant c = (Variant) o;
      return (equal(id, c.id)
           && equal(aaRef, c.aaRef)
           && equal(aaAlt, c.aaAlt)
           && equal(unitprotAaPos, c.unitprotAaPos)
           && equal(aaPos, c.aaPos)
           && equal(sift, c.sift)
           && equal(polyphen, c.polyphen)
           && equal(mutTaster, c.mutTaster)
           && equal(phyloP, c.phyloP)
           && equal(thGenomesAC, c.thGenomesAC)
           && equal(thGenomesAF, c.thGenomesAF)
           && equal(gene, c.gene)
      );
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