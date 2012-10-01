package com.charite.nsfp.test.esp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.charite.esp.model.ESP;
import com.charite.esp.parser.ESPParser;
import com.charite.esp.parser.ESPReader;
import com.charite.exception.InvalidFormatException;
import com.charite.exception.ParserException;
import com.charite.model.ChromosomeId;
import com.charite.progress.ProgressListener;

//TODO: Test the ProgressListener
public class ESPParserTest {
  private static final List<ESP> espList = new ArrayList<ESP>() {
    private static final long serialVersionUID = 1733347950107713342L;
  {
    add(new ESP(new ChromosomeId((short) 2, 41612, 'T', 'C'), (short)  2, (short) 11880, (float) 1.6835016E-4));
    add(new ESP(new ChromosomeId((short) 2, 41615, 'T', 'G'), (short)  1, (short) 11877, (float) 8.419635E-5));
    add(new ESP(new ChromosomeId((short) 2, 41647, 'G', 'A'), (short) 42, (short) 11778, (float) 0.0035659703));
    add(new ESP(new ChromosomeId((short) 2, 41686, 'C', 'T'), (short) 11, (short)  4555, (float) 0.0024149287));
    add(new ESP(new ChromosomeId((short) 2, 45422, 'T', 'A'), (short)  1, (short) 12169, (float) 8.217602E-5));
    add(new ESP(new ChromosomeId((short) 2, 45480, 'T', 'C'), (short)  1, (short) 12383, (float) 8.0755875E-5));
  }};
  
  @Test
  public void testParser() throws Exception {
    final boolean functionCalled[] = { false, false };

    final AtomicInteger idx = new AtomicInteger();
    ESPParser parser = new ESPParser(new ESPReader() {
      
      @Override
      public boolean read(final ESP esp) {
        assertEquals(espList.get(idx.getAndIncrement()), esp);
        return true;
      }
      
      @Override
      public void setUp() {
        functionCalled[0] = true;
      }
      
      @Override
      public void end() {
        functionCalled[1] = true;
      }
    });
    
    File file = new File("src/test/resources/esp_unittest.chr");
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
    
    assertEquals(idx.get(), espList.size());
    assertTrue(functionCalled[0]);
    assertTrue(functionCalled[1]);
  }
  
  @Test
  public void testFileNotExists() throws Exception {
    boolean doesNotExists = false;
    ESPParser parser = new ESPParser(new ESPReader() {
      
      @Override
      public boolean read(final ESP esp) {
        return true;
      }
      
      @Override
      public void setUp() {
      }
      
      @Override
      public void end() {
      }
    });
    
    File file = new File("src/test/resources/esp_unittest.ch");
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
      new ESPParser(null);
    }
    catch (ParserException e) {
      nullReader = true;
    }
    
    assertTrue(nullReader);
  }
  
  @Test
  public void testParserEmptyFile() throws Exception {
    boolean emptyFile = false;
    ESPParser parser = new ESPParser(new ESPReader() {
      
      @Override
      public boolean read(final ESP esp) {
        return true;
      }
      
      @Override
      public void setUp() {
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
    ESPParser parser = new ESPParser(new ESPReader() {
      
      @Override
      public boolean read(final ESP esp) {
        return true;
      }
      
      @Override
      public void setUp() {
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
 
  @Test
  public void testParserDuplicatedEntry() throws Exception {
    final boolean functionCalled[] = { false, false };

    final AtomicInteger idx = new AtomicInteger();
    ESPParser parser = new ESPParser(new ESPReader() {
      
      @Override
      public boolean read(final ESP esp) {
        assertEquals(espList.get(idx.getAndIncrement()).toString(), esp.toString());
        return true;
      }
      
      @Override
      public void setUp() {
        functionCalled[0] = true;
      }
      
      @Override
      public void end() {
        functionCalled[1] = true;
      }
    });
    
    File file = new File("src/test/resources/esp_unittest_duplicated_short.chr");
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
    
    assertEquals(idx.get(), espList.size());
    assertTrue(functionCalled[0]);
    assertTrue(functionCalled[1]);
  }
}
