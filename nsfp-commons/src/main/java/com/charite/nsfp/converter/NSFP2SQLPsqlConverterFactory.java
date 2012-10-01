package com.charite.nsfp.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.charite.exception.ConverterException;
import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;
import com.charite.nsfp.parser.NSFPParser;
import com.charite.nsfp.parser.NSFPReader;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.ThirdPartyConverter;
import com.charite.thirdpartydb.ThirdPartyConverterFactory;

public class NSFP2SQLPsqlConverterFactory implements ThirdPartyConverterFactory {
  @Value("${download.cachelocation}")
  private String cacheLocation = null;

  @Value("${database.psqllocation}")
  private String psql = null;
  
  @Value("${database.hostname}")
  private String hostname = null;
  
  @Value("${database.user}")
  private String user = null;
  
  @Value("${database.password}")
  private String password = null;

  @Value("${database.name}")
  private String database = null;
  
  @Override
  public ThirdPartyConverter getConverter() {
    return new NSFP2PsqlConverter(cacheLocation, psql, hostname, user, password, database);
  }
}

class NSFP2PsqlConverter implements NSFPReader, ThirdPartyConverter {
  private final String cacheLocation;

  private final String psql;
  
  private final String hostname;
  
  private final String user;
  
  private final String password;

  private final String database;

  private FileWriter outGene    = null;
  private FileWriter outVariant = null;
  private File fileGene         = null;
  private File fileVariant      = null;
  
  public NSFP2PsqlConverter(final String cacheLocation, final String psql, final String hostname, final String user, final String password, final String database) {
    this.cacheLocation = cacheLocation;
    this.psql          = psql;
    this.hostname      = hostname;
    this.user          = user;
    this.password      = password;
    this.database      = database;
  }
  
  @Override
  public void setUp() {
    if (!new File(psql).exists())
      throw new ConverterException("Can't execute the psql command. Program does not exists.");

    try {
      fileGene = new File(cacheLocation, new StringBuilder().append("sqlgene_").append(UUID.randomUUID()).toString() + ".sql"); 
      outGene = new FileWriter(fileGene);
      
      fileVariant = new File(cacheLocation, new StringBuilder().append("sqlvariant_").append(UUID.randomUUID()).toString() + ".sql"); 
      outVariant = new FileWriter(fileVariant);
    }
    catch (IOException e) {
      new ConverterException("Problems to open the temporary file to process the data", e);
    }
  }

  @Override
  public boolean read(Variant variant) {
    if (outVariant == null || fileVariant == null)
      throw new ConverterException("Problems to open the temporary file to process the data");
    
    //TODO: MUST CHECK IF IT HAS NULL ELEMENTS!!
    String str = (new StringBuilder())
      .append(variant.getId().getChromosome()).append("|")
      .append(variant.getId().getPosition()).append("|")
      .append(variant.getId().getRef()).append("|")
      .append(variant.getId().getAlt()).append("|")
      .append(variant.getAaRef()).append("|")
      .append(variant.getAaAlt()).append("|")
      .append(variant.getUnitprotAaPos()).append("|")
      .append(variant.getAaPos()).append("|")
      .append(variant.getSift()).append("|")
      .append(variant.getPolyphen()).append("|")
      .append(variant.getMutTaster()).append("|")
      .append(variant.getPhyloP()).append("|")
      .append(variant.getThGenomesAC()).append("|")
      .append(variant.getThGenomesAF()).append("|")
      .append(variant.getGene().getId()).append("\n").toString();
    try {
      outVariant.write(str);
    }
    catch (IOException e) {
      return false;
    }

    return true;
  }

  @Override
  public boolean read(Gene gene) {
    if (outGene == null || fileGene == null)
      throw new ConverterException("Problems to open the temporary file to process the data");
    
    //TODO: MUST CHECK IF IT HAS NULL ELEMENTS!!
    String str = (new StringBuilder())
      .append(gene.getId()).append("|")
      .append(gene.getGeneName()).append("|")
      .append(gene.getUniprotAcc()).append("|")
      .append(gene.getUniprotId()).append("|")
      .append(gene.getCdsStrand()).append("|")
      .append(gene.getEnsemblGeneId()).append("|")
      .append(gene.getEnsemblTranscriptId()).append("\n").toString();
    try {
      outGene.write(str);
    }
    catch (IOException e) {
      return false;
    }

    return true;
  }

  @Override
  public void end() {
    try {
      writeGene();
      writeVariant();
    }
    finally {
      fileGene.delete();
      fileVariant.delete();
      outGene    = null;
      outVariant = null;
    }
  }

  @Override
  public void convert(File file, ProgressListener progress) {
    NSFPParser parser = new NSFPParser(this);
    
    try {
      parser.parse(file, progress);
    }
    catch (Exception e) {
      throw new ConverterException("Can't convert the NSFP file.", e);
    }
  }
  
  private void writeVariant() {
    if (outVariant == null || fileVariant == null)
      throw new ConverterException("Problems to open the temporary file to process the data");

    try {
      outVariant.close();
    }
    catch (IOException e) {
      throw new ConverterException("Problems to close the Temporary file", e);
    }

    String sql = (new StringBuilder())
        .append("\\COPY variant (chromosome, position, ref, alt, aaref, aaalt, uniprot_aapos, aapos, " +
        		"sift, polyphen, mut_taster, phyloP, ThGenomes_AC, ThGenomes_AF, gene_gene_id) FROM '")
        .append(fileVariant.getAbsolutePath()).append("' WITH DELIMITER '|'").toString();

    String env[]  = { "PGPASSWORD=" + password };
    String args[] = { psql, "-U", user, "-h", hostname, "-c", sql, database};
    Runtime rt = Runtime.getRuntime();

    try {
      Process p = rt.exec(args, env);
      p.waitFor();
      p.destroy();
    }
    catch (IOException e) {
      throw new ConverterException("Problems to run the PSQL program", e);
    }
    catch (InterruptedException e) {
      throw new ConverterException("Problems to wait the psl", e);
    }
  }

  private void writeGene() {
    if (outGene == null || fileGene == null)
      throw new ConverterException("Problems to open the temporary file to process the data");

    try {
      outGene.close();
    }
    catch (IOException e) {
      throw new ConverterException("Problems to close the Temporary file", e);
    }

    String sql = (new StringBuilder())
        .append("\\COPY gene (gene_id, genename, uniprot_acc, uniprot_id, cds_strand, ensembl_geneid, ensembl_transcript_id) FROM '")
        .append(fileGene.getAbsolutePath()).append("' WITH DELIMITER '|'").toString();

    String env[]  = { "PGPASSWORD=" + password };
    String args[] = { psql, "-U", user, "-h", hostname, "-c", sql, database};
    Runtime rt = Runtime.getRuntime();

    try {
      Process p = rt.exec(args, env);
      p.waitFor();
      p.destroy();
    }
    catch (IOException e) {
      throw new ConverterException("Problems to run the PSQL program", e);
    }
    catch (InterruptedException e) {
      throw new ConverterException("Problems to wait the psl", e);
    }
  }
}
