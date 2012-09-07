package com.charite.snv.model;

import com.charite.enums.Genotype;
import com.charite.enums.VariantType;
import com.charite.esp.model.ESP;
import com.charite.nsfp.model.Variant;

public class SNV {
  private Short chromosome        = null;
  private Integer position        = null;
  private String ref              = null;
  private String alt              = null;
  private VariantType variantType = VariantType.UNKNOWN;
  private Genotype genotype       = Genotype.GENOTYPE_UNKNOWN;
  private Integer genotypeQuality = null;
  private String geneName         = null;
  private String refSeqId         = null;
  private String exon             = null;
  private String cdsMutation      = null;
  private String aaMutation       = null;
  private ESP esp                 = null;
  private Variant variant         = null;
  
  public SNV() {  
  }
  
  public SNV(final Short chromosome, final Integer position, final String ref, final String alt, final VariantType variantType,
    final Genotype genotype, final Integer genotypeQuality, final String geneName, final String refSeqId, final String exon,
    final String cdsMutation, final String aaMutation) {
    this.chromosome      = chromosome;
    this.position        = position;
    this.ref             = ref;
    this.alt             = alt;
    this.variantType     = variantType;
    this.genotype        = genotype;
    this.genotypeQuality = genotypeQuality;
    this.geneName        = geneName;
    this.refSeqId        = refSeqId;
    this.exon            = exon;
    this.cdsMutation     = cdsMutation;
    this.aaMutation      = aaMutation;
  }
  
  public SNV(final Short chromosome, final Integer position, final String ref, final String alt) {
    this.chromosome  = chromosome;
    this.position    = position;
    this.ref         = ref;
    this.alt         = alt;
  }
  
  public Short getChromosome() {
    return chromosome;
  }
  
  public void setChromosome(Short chromosome) {
    this.chromosome = chromosome;
  }
  
  public Integer getPosition() {
    return position;
  }
  
  public void setPosition(Integer position) {
    this.position = position;
  }
  
  public String getRef() {
    return ref;
  }
  
  public void setRef(String ref) {
    this.ref = ref;
  }
  
  public String getAlt() {
    return alt;
  }
  
  public void setAlt(String alt) {
    this.alt = alt;
  }

  public VariantType getVariantType() {
    return variantType;
  }

  public void setVariantType(VariantType variantType) {
    this.variantType = variantType;
  }

  public Genotype getGenotype() {
    return genotype;
  }

  public void setGenotype(Genotype genotype) {
    this.genotype = genotype;
  }

  public Integer getGenotypeQuality() {
    return genotypeQuality;
  }

  public void setGenotypeQuality(Integer genotypeQuality) {
    this.genotypeQuality = genotypeQuality;
  }

  public String getGeneName() {
    return geneName;
  }

  public void setGeneName(String geneName) {
    this.geneName = geneName;
  }

  public String getRefSeqId() {
    return refSeqId;
  }

  public void setRefSeqId(String refSeqId) {
    this.refSeqId = refSeqId;
  }

  public String getExon() {
    return exon;
  }

  public void setExon(String exon) {
    this.exon = exon;
  }

  public String getCdsMutation() {
    return cdsMutation;
  }

  public void setCdsMutation(String cdsMutation) {
    this.cdsMutation = cdsMutation;
  }

  public String getAaMutation() {
    return aaMutation;
  }

  public void setAaMutation(String aaMutation) {
    this.aaMutation = aaMutation;
  }
  
  public ESP getESP() {
    return esp;
  }

  public void setESP(ESP esp) {
    this.esp = esp;
  }

  public Variant getVariant() {
    return variant;
  }

  public void setVariant(Variant variant) {
    this.variant = variant;
  }
  
  boolean isNonPathogenic() {
    return isPredictedNonMissensePath();
  }

  boolean isPredictedNonMissensePath() {
    switch (variantType) {
      case FS_DELETION:
      case FS_INSERTION:
      case NON_FS_SUBSTITUTION:
      case FS_SUBSTITUTION:
      case NONSENSE:
      case SPLICING:
      case STOPGAIN:
      case STOPLOSS:
        return true;
      default:
    }

    return false;
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("[+] VCF ==================================================\n")
           .append("Chromosome                     : ").append(chromosome).append("\n")
           .append("Position                       : ").append(position).append("\n")
           .append("Reference Sequence             : ").append(ref).append("\n")
           .append("Alt Sequence                   : ").append(alt).append("\n")
           .append("Variant                        : ").append(variantType).append("\n")
           .append("Genotype                       : ").append(genotype).append("\n")
           .append("Genotype Quality               : ").append(genotypeQuality).append("\n")
           .append("Gene Name                      : ").append(geneName).append("\n")
           .append("Reference Sequence ID          : ").append(refSeqId).append("\n")
           .append("Exon                           : ").append(exon).append("\n")
           .append("CDS Mutation                   : ").append(cdsMutation).append("\n")
           .append("AA Mutations                   : ").append(aaMutation).append("\n")
           .append("Is Predicted non missense Path : ").append(isPredictedNonMissensePath()).append("\n")
           .append(esp).append("\n").append(variant).append("\n");
    
    return builder.toString();
  }
  
  private static int hash(Object o) {
    return o == null ? 0 : o.hashCode();
  }
  
  public int hashCode() {
    int hash = 0;
    hash = 31 * hash + hash(chromosome);
    hash = 31 * hash + hash(position);
    hash = 31 * hash + hash(ref);
    hash = 31 * hash + hash(alt);
    hash = 31 * hash + hash(variantType);
    hash = 31 * hash + hash(genotype);
    hash = 31 * hash + hash(genotypeQuality);
    hash = 31 * hash + hash(geneName);
    hash = 31 * hash + hash(refSeqId);
    hash = 31 * hash + hash(exon);
    hash = 31 * hash + hash(cdsMutation);
    hash = 31 * hash + hash(aaMutation);
    hash = 31 * hash + hash(esp);
    hash = 31 * hash + hash(variant);

    return hash;    
  }
  
  private static boolean equal(Object o, Object another)
  {
    return o == null ? another == null : o.equals(another);
  }

  public boolean equals(Object o) {
    if (o instanceof SNV) {
      SNV c = (SNV) o;
      return (equal(chromosome, c.chromosome) && equal(position, c.position) && equal(ref, c.ref) && equal(alt, c.alt)
        && equal(variantType, c.variantType) && equal(genotype, c.genotype) && equal(genotypeQuality, c.genotypeQuality) 
        && equal(geneName, c.geneName) && equal(refSeqId, c.refSeqId) && equal(exon, c.exon) && equal(cdsMutation, c.cdsMutation)
        && equal(aaMutation, c.aaMutation) && equal(esp, c.esp) && equal(variant, c.variant));
    }
    
    return false;
  }
}