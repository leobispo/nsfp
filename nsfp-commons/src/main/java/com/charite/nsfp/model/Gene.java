package com.charite.nsfp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gene")
public class Gene {
  @Id
  @Column(name = "gene_id")
  private Long id;
  
  @Column(name = "genename", length = 64)
  private String geneName;
  
  @Column(name = "uniprot_acc", length = 64)
  private String uniprotAcc;
  
  @Column(name = "uniprot_id", length = 64)
  private String uniprotId;
  
  @Column(name = "cds_strand")
  private Character cdsStrand;
  
  @Column(name = "ensembl_geneid", length = 255)
  private String ensemblGeneId;
  
  @Column(name = "ensembl_transcript_id", length = 255)
  private String ensemblTranscriptId;

  public Gene() {
  }
  
  public Gene(final Long id, final String geneName, final String uniprotAcc, final String uniprotId,
    final Character cdsStrand, final String ensemblGeneId, final String ensemblTranscriptId) {
    this.id = id;
    this.geneName = geneName;
    this.uniprotAcc = uniprotAcc;
    this.uniprotId = uniprotId;
    this.cdsStrand = cdsStrand;
    this.ensemblGeneId = ensemblGeneId;
    this.ensemblTranscriptId = ensemblTranscriptId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getGeneName() {
    return geneName;
  }

  public void setGeneName(String geneName) {
    this.geneName = geneName;
  }

  public String getUniprotAcc() {
    return uniprotAcc;
  }

  public void setUniprotAcc(String uniprotAcc) {
    this.uniprotAcc = uniprotAcc;
  }

  public String getUniprotId() {
    return uniprotId;
  }

  public void setUniprotId(String uniprotId) {
    this.uniprotId = uniprotId;
  }

  public Character getCdsStrand() {
    return cdsStrand;
  }

  public void setCdsStrand(Character cdsStrand) {
    this.cdsStrand = cdsStrand;
  }

  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public String getEnsemblTranscriptId() {
    return ensemblTranscriptId;
  }

  public void setEnsemblTranscriptId(String ensemblTranscriptId) {
    this.ensemblTranscriptId = ensemblTranscriptId;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("[+] Gene ==================================================\n")
           .append("Gene Id               : ").append(id).append("\n")
           .append("Gene Name             : ").append(geneName).append("\n")
           .append("Unit. Prot. ACC       : ").append(uniprotAcc).append("\n")
           .append("Unit. Prot. Id        : ").append(uniprotId).append("\n")
           .append("CDS Strand            : ").append(cdsStrand).append("\n")
           .append("Ensembl Gene Id       : ").append(ensemblGeneId).append("\n")
           .append("Ensembl Transcript Id : ").append(ensemblTranscriptId).append("\n");
    
    return builder.toString();
  }
  
  @Override
  public int hashCode() {
    int hash = 0;
    hash = 31 * hash + hash(id);
    hash = 31 * hash + hash(geneName);
    hash = 31 * hash + hash(uniprotAcc);
    hash = 31 * hash + hash(uniprotId);
    hash = 31 * hash + hash(cdsStrand);
    hash = 31 * hash + hash(ensemblGeneId);
    hash = 31 * hash + hash(ensemblTranscriptId);

    return hash;    
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof Gene) {
      Gene c = (Gene) o;
      return (equal(id, c.id) 
           && equal(geneName, c.geneName)
           && equal(uniprotAcc, c.uniprotAcc)
           && equal(uniprotId, c.uniprotId)
           && equal(cdsStrand, c.cdsStrand) 
           && equal(ensemblGeneId, c.ensemblGeneId)
           && equal(ensemblTranscriptId, c.ensemblTranscriptId)
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