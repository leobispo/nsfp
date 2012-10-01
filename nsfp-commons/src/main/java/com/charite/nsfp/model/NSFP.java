package com.charite.nsfp.model;

import com.charite.filter.MapReduceKey;
import com.charite.snv.model.SNV;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("NSFP")
public class NSFP implements MapReduceKey {
  private final Short chromosome;
  private final Integer position;
  private final String ref;
  private final String alt;
  private final String uniprotId;
  private final String ensemblGeneId;
  private final String geneName;
  private final Integer aaPos;
  private final Float sift;
  private final Float polyphenHVAR;
  private final Float phyloP;
  private final Short thGenomesAC;
  private final Float thGenomesAF;
  
  @XStreamOmitField
  private final SNV snv;

  public NSFP(SNV snv) {
    chromosome    = snv.getChromosome();
    position      = snv.getPosition();
    ref           = Character.toString(snv.getRef().charAt(0));
    alt           = Character.toString(snv.getAlt().charAt(0));
    uniprotId     = "-";
    ensemblGeneId = "-";
    geneName      = snv.getGeneName();
    aaPos         = -1;
    sift          = -2f;
    polyphenHVAR  = -2f;
    phyloP        = -2f;
    thGenomesAC   = -3;
    thGenomesAF   = -3f;

    this.snv = snv;
  }
  
  public NSFP(SNV snv, Variant variant) {
    chromosome    = snv.getChromosome();
    position      = snv.getPosition();
    ref           = snv.getRef();
    alt           = snv.getAlt();
    uniprotId     = variant.getGene().getUniprotId();
    ensemblGeneId = variant.getGene().getEnsemblGeneId();
    geneName      = variant.getGene().getGeneName();
    aaPos         = variant.getAaPos();
    sift          = variant.getSift();
    polyphenHVAR  = variant.getPolyphen();
    phyloP        = variant.getPhyloP();
    thGenomesAC   = variant.getThGenomesAC();
    thGenomesAF   = variant.getThGenomesAF();

    this.snv = snv;
  }

  public Short getChromosome() {
    return chromosome;
  }

  public Integer getPosition() {
    return position;
  }

  public String getRef() {
    return ref;
  }

  public String getAlt() {
    return alt;
  }

  public String getUniprotId() {
    return uniprotId;
  }

  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public String getGeneName() {
    return geneName;
  }

  public Integer getAaPos() {
    return aaPos;
  }

  public Float getSift() {
    return sift;
  }

  public Float getPolyphenHVAR() {
    return polyphenHVAR;
  }

  public Float getPhyloP() {
    return phyloP;
  }

  public Short getThGenomesAC() {
    return thGenomesAC;
  }

  public Float getThGenomesAF() {
    return thGenomesAF;
  }

  public SNV getSnv() {
    return snv;
  }

  @Override
  public String key() {
    // TODO Auto-generated method stub
    return null;
  }
  
  //TODO: IMPLEMENT TO STRING AND ALL OTHER NEEDED METHODS
}
