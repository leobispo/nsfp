package com.charite.nsfp.test.vcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.charite.enums.Genotype;
import com.charite.enums.VariantType;
import com.charite.exception.InvalidFormatException;
import com.charite.exception.ParserException;
import com.charite.progress.ProgressListener;
import com.charite.snv.model.SNV;
import com.charite.vcf.parser.VCFParser;
import com.charite.vcf.reader.VCFReader;

//TODO: Test the ProgressListener
public class VCFParserTest {

  private static final List<SNV> vcfList = new ArrayList<SNV>() {
    private static final long serialVersionUID = 684900161266848905L;
  {
    add(new SNV((short) 23, 14370    , "G"  , "A"     , VariantType.UNKNOWN     , Genotype.GENOTYPE_HOMOZYGOUS_ALT, 48, null   , null       , null    , null       , null      ));
    add(new SNV((short) 24, 17330    , "T"  , "A"     , VariantType.UNKNOWN     , Genotype.GENOTYPE_HOMOZYGOUS_REF, 49, null   , null       , null    , null       , null      ));
    add(new SNV((short) 25, 1110696  , "A"  , "G,T"   , VariantType.MISSENSE    , Genotype.GENOTYPE_UNKNOWN       , 21, null   , null       , null    , null       , null      ));
    add(new SNV((short) 20, 1234567  , "GTC", "G,GTCT", VariantType.SYNONYMOUS  , Genotype.GENOTYPE_HETEROZYGOUS  , 35, "gene1", null       , null    , null       , null      ));
    add(new SNV((short) 1 , 186050417, "A"  , "G"     , VariantType.FS_DELETION , Genotype.GENOTYPE_HOMOZYGOUS_ALT, 99, "HMCN1", "NM_031935", "exon56", "c.8678A>G", "p.E2893G"));
    add(new SNV((short) 23, 186050417, "A"  , "G"     , VariantType.STOPLOSS    , Genotype.GENOTYPE_HOMOZYGOUS_ALT, 99, "HMCN1", "NM_031935", "exon56", "c.8678A>G", null      ));
  }};

  @Test
  public void testParser() throws Exception {
    final boolean functionCalled[] = { false, false };
    
    final AtomicInteger idx = new AtomicInteger();
    
    VCFParser parser = new VCFParser(new VCFReader() {

      @Override
      public void setUp(String version) {
        functionCalled[0] = true;
        assertEquals(version, "v4.1");
      }

      @Override
      public boolean read(SNV vcf) {
        assertEquals(vcfList.get(idx.getAndIncrement()), vcf);
        return true;
      }

      @Override 
      public void end(List<String> header) {
        functionCalled[1] = true;
        
        final List<String> expectedHeader = new ArrayList<String>() {
          private static final long serialVersionUID = 1614052673293704766L;
        {
          add("##fileformat=VCFv4.1");
          add("##fileDate=20090805");
          add("##source=myImputationProgramV3.1");
          add("##reference=file:///seq/references/1000GenomesPilot-NCBI36.fasta");
          add("##contig=");
          add("##phasing=partial");
          add("##INFO=");
          add("##INFO=");
          add("##INFO=");
          add("##INFO=");
          add("##INFO=");
          add("##INFO=");
          add("##FILTER=");
          add("##FILTER=");
          add("##FORMAT=");
          add("##FORMAT=");
          add("##FORMAT=");
          add("##FORMAT=");
          add("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tNA00001\tNA00002\tNA00003");
        }};

        assertEquals(expectedHeader, header);
      }
    });

    File file = new File("src/test/resources/test.vcf41");
    parser.parse(file, new ProgressListener() { 
      @Override
      public void start(String uid, long dataSize) {
      }
      
      @Override
      public void progress(String uid, int percent, long seconds, long currentDataSize) {
      }
      
      @Override
      public void failed(String uid, String message) {
      }
      
      @Override
      public void end(String uid) {
      }
    });
    
    assertEquals(idx.get(), vcfList.size());
    assertTrue(functionCalled[0]);
    assertTrue(functionCalled[1]);
  }
  
  @Test
  public void testFileNotExists() throws Exception {
    boolean doesNotExists = false;
    VCFParser parser = new VCFParser(new VCFReader() {
      
      @Override
      public void setUp(String version) {
      }
      
      @Override
      public boolean read(SNV vcf) {
        return false;
      }
      
      @Override
      public void end(List<String> header) {
      }
    });
    
    File file = new File("src/test/resources/test.vcf4");
    try {
      parser.parse(file, new ProgressListener() {
        @Override
        public void start(String uid, long dataSize) {
        }
        
        @Override
        public void progress(String uid, int percent, long seconds, long currentDataSize) {
        }
        
        @Override
        public void failed(String uid, String message) {
        }
        
        @Override
        public void end(String uid) {
        }
      });
    }
    catch (FileNotFoundException e) {
      doesNotExists = true;
    }
    
    assertTrue(doesNotExists);
  }

  @Test
  public void testNullWriter() {
    boolean nullWriter = false;
    try {
      new VCFParser(null);
    }
    catch (ParserException e) {
      nullWriter = true;
    }
    
    assertTrue(nullWriter);
  }
  
  @Test
  public void testParserEmptyFile() throws Exception {
    boolean emptyFile = false;
    VCFParser parser = new VCFParser(new VCFReader() {
      
      @Override
      public void setUp(String version) {
      }
      
      @Override
      public boolean read(SNV vcf) {
        return false;
      }
      
      @Override
      public void end(List<String> header) {
      }
    });
    
    File file = new File("src/test/resources/empty.txt");
    try {
      parser.parse(file, new ProgressListener() {
        @Override
        public void start(String uid, long dataSize) {
        }
        
        @Override
        public void progress(String uid, int percent, long seconds, long currentDataSize) {
        }
        
        @Override
        public void failed(String uid, String message) {
        }
        
        @Override
        public void end(String uid) {
        }
      });
    }
    catch (InvalidFormatException e) {
      emptyFile = true;
    }
    
    assertTrue(emptyFile);
  }  
}
