package com.charite.nsfp.test.nsfp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.charite.exception.InvalidFormatException;
import com.charite.exception.ParserException;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;
import com.charite.nsfp.parser.NSFPParser;
import com.charite.nsfp.reader.NSFPReader;
import com.charite.progress.ProgressListener;

public class NSFPParserTest {
  private static final List<Variant> variantList = new ArrayList<Variant>() {
    private static final long serialVersionUID = -1262041198651514757L;
    {
      add(new Variant(new ChromosomeId((short) 1, 35138, 'T', 'A'), 'X', 'Y', 2, 86, -5.3f, -1f, -1f, 0.593f, (short) -1, -1f, new Gene(0L, "FAM138A", "None", "test", '-', "ENSG00000237613", "ENST00000417324")));
      add(new Variant(new ChromosomeId((short) 2, 35139, 'T', 'A'), 'X', 'L', 4, 86, -1f, -1f, 7.75f, -1.28f, (short) -1, -1f, new Gene(1L, "FAM13AA", ".", "test2", '+', "ENSG00000237613", "ENST00000417324")));
      add(new Variant(new ChromosomeId((short) 1, 35140, 'A', 'C'), 'X', 'E', -1, 86, 7f, -1f, -1f, 0.699f, (short) -1, -1f, new Gene(0L, "FAM138A", "None", "test", '-', "ENSG00000237613", "ENST00000417324")));
      add(new Variant(new ChromosomeId((short) 1, 35140, 'A', 'T'), 'X', 'K', -1, 86, -1f, -1f, -1f, 0.699f, (short) -1, 6.66f, new Gene(2L, "FAM137A", ".", ".", '+', "ENSG00000237613", "ENST00000417324")));
      add(new Variant(new ChromosomeId((short) 1, 35142, 'G', 'A'), 'T', 'M', 8, 85, 0f, -1f, -1f, -0.102f, (short) -1, -1f, new Gene(3L, "FAM139A", "Test", "test3", '?', "ENSG00000237613", "ENST00000417324")));
      add(new Variant(new ChromosomeId((short) 5, 35142, 'G', 'C'), 'T', 'R', -1, 85, 0f, -1f, -1f, -0.102f, (short) -1, -1f, new Gene(4L, "FAM111A", ".", "test5", '+', "ENSG00000237615", "ENST00000417320")));
    }
  };
    
  @Test
  public void testParser() throws Exception {
    final boolean functionCalled[] = { false, false };

    final AtomicInteger idx = new AtomicInteger();
    NSFPParser parser = new NSFPParser(new NSFPReader() {
      
      @Override
      public void setUp() {
        functionCalled[0] = true;
      }
      
      @Override
      public boolean read(Gene gene) {
        return true;
      }
      
      @Override
      public boolean read(Variant variant) {
        Variant v = variantList.get(idx.getAndIncrement());
        v.getGene().setId(variant.getGene().getId());
        assertEquals(v, variant);
        return true;
      }
      
      @Override
      public void end() {
        functionCalled[1] = true;
      }
    });
    
    File file = new File("src/test/resources/nsfp_unittest.chr");
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

    assertTrue(functionCalled[0]);
    assertTrue(functionCalled[1]);
  }
  
  @Test
  public void testFileNotExists() throws Exception {
    boolean doesNotExists = false;
    NSFPParser parser = new NSFPParser(new NSFPReader() {
      @Override
      public void setUp() {
      }
      
      @Override
      public boolean read(Gene gene) {
        return false;
      }
      
      @Override
      public boolean read(Variant variant) {
        return false;
      }
      
      @Override
      public void end() {
      }
    });
    
    File file = new File("src/test/resources/nsfp_unittest.ch");
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
  public void testNullReader() {
    boolean nullReader = false;
    try {
      new NSFPParser(null);
    }
    catch (ParserException e) {
      nullReader = true;
    }
    
    assertTrue(nullReader);
  }
  
  @Test
  public void testParserEmptyFile() throws Exception {
    boolean emptyFile = false;
    NSFPParser parser = new NSFPParser(new NSFPReader() {
      
      @Override
      public void setUp() {
      }
      
      @Override
      public boolean read(Gene gene) {
        return false;
      }
      
      @Override
      public boolean read(Variant variant) {
        return false;
      }
      
      @Override
      public void end() {
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
  
  @Test
  public void testParserInvalidHeader() throws Exception {
    boolean invalidHeader = false;
    NSFPParser parser = new NSFPParser(new NSFPReader() {
      
      @Override
      public void setUp() {
      }
      
      @Override
      public boolean read(Gene gene) {
        return false;
      }
      
      @Override
      public boolean read(Variant variant) {
        return false;
      }
      
      @Override
      public void end() {
      }
    });
    
    File file = new File("src/test/resources/begin_header_not_found.txt");
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
      invalidHeader = true;
    }
    
    assertTrue(invalidHeader);
    
    invalidHeader = false;
    file = new File("src/test/resources/header_column_not_found.txt");
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
      invalidHeader = true;
    }
    
    assertTrue(invalidHeader);
  }
}
