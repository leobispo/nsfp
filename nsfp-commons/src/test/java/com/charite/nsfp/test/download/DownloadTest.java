package com.charite.nsfp.test.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.charite.download.DownloadFuture;
import com.charite.download.DownloadManager;
import com.charite.download.DownloadResult;
import com.charite.exception.DownloadException;
import com.charite.progress.ProgressListener;

public class DownloadTest {
  private static byte[] createChecksum(File file) throws Exception {
    InputStream is = new FileInputStream(file);

    byte buffer[] = new byte[1024];
    MessageDigest complete = MessageDigest.getInstance("MD5");
    int numRead;

    do {
      numRead = is.read(buffer);
      if (numRead > 0)
        complete.update(buffer, 0, numRead);
    }
    while (numRead != -1);

    is.close();
    return complete.digest();
  }

  public static String getMD5(File file) throws Exception {
    byte b[] = createChecksum(file);
    String result = "";

    for (int i=0; i < b.length; ++i)
      result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    
    return result;
  }

  @Test
  public void testDownload() throws Exception {
    DownloadManager<DownloadTest> manager = new DownloadManager<DownloadTest>(1);
    
    File file = new File("src/test/resources/test.txt");
    
    assertTrue(file.exists());
    URL url = null;
    url = file.toURI().toURL();
    assertNotNull(url);
    
    manager.enqueueURL(url, "/tmp", this, new ProgressListener() {
      @Override
      public void start(final String uid, final long fileSize) {
      }
      
      @Override
      public void progress(final String uid, final int percent, final long seconds, final long downloadedSize) {
      }
      
      @Override
      public void failed(final String uid, final String message) {
      }

      @Override
      public void end(String uid) {
      }
    });
    
    DownloadFuture<DownloadTest> future = manager.start();
    List<DownloadResult<DownloadTest>> result = future.get();
    
    assertTrue(future.isDone());
    assertNotNull(result);
    assertEquals(result.size(), 1);
    
    assertFalse(result.get(0).isError());
    assertEquals(result.get(0).fileName(), "/tmp/test.txt");
    File fileResult = new File("/tmp/test.txt");
    
    assertTrue(fileResult.exists());
    assertEquals(getMD5(fileResult), getMD5(file));
    
    fileResult.delete();
    
    assertEquals(future.get(), result);
  }

  @Test
  public void testMultipleFiles() throws Exception {
    DownloadManager<DownloadTest> manager = new DownloadManager<DownloadTest>(3);
    
    List<File> files = new ArrayList<File>(); 
    
    for (int i = 1; i < 4; ++i) {
      File file = new File("src/test/resources/test" + i + ".txt");
      files.add(file);
      
      assertTrue(file.exists());
      URL url = null;
      url = file.toURI().toURL();
      assertNotNull(url);
    
      manager.enqueueURL(url, "/tmp", this, new ProgressListener() {
        @Override
        public void start(final String uid, final long fileSize) {
        }
        
        @Override
        public void progress(final String uid, final int percent, final long seconds, final long downloadedSize) {
        }
        
        @Override
        public void failed(final String uid, final String message) {
        }

        @Override
        public void end(String uid) {
        }
      });
    }
    
    DownloadFuture<DownloadTest> future = manager.start();
    List<DownloadResult<DownloadTest>> result = future.get();

    assertNotNull(result);
    assertEquals(result.size(), 3);
    for (int i = 0; i < result.size(); ++i) {
      assertEquals(result.get(i).fileName(), "/tmp/test" + (i + 1) + ".txt");
      File fileResult = new File("/tmp/test" + (i + 1) + ".txt");
      assertTrue(fileResult.exists());
      assertEquals(getMD5(fileResult), getMD5(files.get(i)));
    
      fileResult.delete(); 
    }
  }
  
  @Test
  public void testMultipLeFileSimultaneous() throws Exception {
    DownloadManager<DownloadTest> manager = new DownloadManager<DownloadTest>(3);
    
    final int execution[] = new int[3];
    List<File> files = new ArrayList<File>(); 
    
    final AtomicInteger ai = new AtomicInteger();
    final AtomicInteger idx = new AtomicInteger();
    for (int i = 1; i < 4; ++i) {
      File file = new File("src/test/resources/test" + i + ".txt");
      files.add(file);
      
      assertTrue(file.exists());
      URL url = null;
      url = file.toURI().toURL();
      assertNotNull(url);
    
      manager.enqueueURL(url, "/tmp", this, new ProgressListener() {
        int myIdx = idx.getAndIncrement();  
        @Override
        public void start(final String uid, final long fileSize) {
          try {
            if (myIdx == 0)
              Thread.sleep(2000);
            if (myIdx == 1)
              Thread.sleep(1000);
          }
          catch (InterruptedException e) {
          }
        }
      
        @Override
        public void progress(final String uid, final int percent, final long seconds, final long downloadedSize) {
          if (percent == 100)
            execution[ai.getAndIncrement()] = myIdx;
        }
      
        @Override
        public void failed(final String uid, final String message) {
        }

        @Override
        public void end(String uid) {
        }
      });
    }
    

    DownloadFuture<DownloadTest> future = manager.start();
    List<DownloadResult<DownloadTest>> result = future.get();

    assertEquals(execution[0], 2);
    assertEquals(execution[1], 1);
    assertEquals(execution[2], 0);
    
    assertNotNull(result);
    assertEquals(result.size(), 3);
    for (int i = 0; i < result.size(); ++i) {
      assertEquals(result.get(i).fileName(), "/tmp/test" + (i + 1) + ".txt");
      File fileResult = new File("/tmp/test" + (i + 1) + ".txt");
      assertTrue(fileResult.exists());
      assertEquals(getMD5(fileResult), getMD5(files.get(i)));
    
      fileResult.delete(); 
    }
  }
  
  @Test
  public void testSimultaneousRange() {
    boolean rangeError = false;
    try {
      new DownloadManager<DownloadTest>(0);
    }
    catch (DownloadException e) {
      rangeError = true;
    }
    
    assertTrue(rangeError);
    
    rangeError = false;
    try {
      new DownloadManager<DownloadTest>(11);
    }
    catch (DownloadException e) {
      rangeError = true;
    }
    
    assertTrue(rangeError);
    
    rangeError = false;
    try {
      new DownloadManager<DownloadTest>(1);
    }
    catch (DownloadException e) {
      rangeError = true;
    }
    
    assertFalse(rangeError);
    
    rangeError = false;
    try {
      new DownloadManager<DownloadTest>(10);
    }
    catch (DownloadException e) {
      rangeError = true;
    }
    
    assertFalse(rangeError);
  }
  
  @Test
  public void testNotFound() throws Exception {
    DownloadManager<DownloadTest> manager = new DownloadManager<DownloadTest>(1);
    final boolean failedCalled[] = new boolean[1];
    
    failedCalled[0] = false;
    manager.enqueueURL(new URL("http://notfound"), "/tmp", this, new ProgressListener() {    
      @Override
      public void start(final String uid, final long fileSize) {
      }
      
      @Override
      public void progress(final String uid, final int percent, final long seconds, final long downloadedSize) {
      }
      
      @Override
      public void failed(final String uid, final String message) {
        failedCalled[0] = true;
      }

      @Override
      public void end(String uid) {
      }
    });
    
    DownloadFuture<DownloadTest> future = manager.start();
    List<DownloadResult<DownloadTest>> result = future.get();
    assertEquals(result.size(), 1);
    assertTrue(result.get(0).isError());
    assertTrue(failedCalled[0]);
  }  
}