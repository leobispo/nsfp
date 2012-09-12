package com.charite.nsfp.test.thirdpartydb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

import com.charite.esp.dao.ESPDao;
import com.charite.esp.model.ESP;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.dao.NSFPDao;
import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.ThirdPartyDatabaseManager;

@Service("DownloadListener")
@Qualifier("DownloadListener")
class ThirdPartyTestDownloadListener implements ProgressListener{

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
}

public class ThirdPartyDBTest {
  private static final List<ESP> espList = new ArrayList<ESP>() {
    private static final long serialVersionUID = 1733347950107713342L;
  {
    add(new ESP(new ChromosomeId((short) 23, 200816, 'T', 'C'), (short)  1, (short) 12997, (float) 7.694083E-5));
    add(new ESP(new ChromosomeId((short) 1, 69428, 'G', 'T'), (short)  327, (short) 10343, (float) 0.031615585));
  }};
  
  private static final HashMap<String, Variant> variants = new HashMap<String, Variant>() {
    private static final long serialVersionUID = -1262041198651514757L;
    {
      put("FAM138A", new Variant(new ChromosomeId((short)  1, 35138, 'T', 'A'), 'X', 'Y', 2,  86, -5.3f, -1f, -1f, 0.593f, (short) -1, -1f, new Gene(0L,       "FAM138A", "None", "test", '-', "ENSG00000237613", "ENST00000417324")));
      put("RP11-631M21.2", new Variant(new ChromosomeId((short) 10, 92997, 'C', 'A'), 'X', 'Y', -1, 373, -1f, -1f, -1f, 0.181f, (short) -1, -1f, new Gene(1L, "RP11-631M21.2", ".", ".", '-', "ENSG00000173876", "ENST00000447903")));
    }
  };
  @Test
  public void testDownload() throws Exception {
    File file = new File("src/test/resources/ESP6500.snps.txt.tar.gz");

    assertTrue(file.exists());
    URL url = null;
    url = file.toURI().toURL();
    assertNotNull(url);
    
    File file1 = new File("src/test/resources/dbNSFP2.0b3.zip");

    assertTrue(file1.exists());
    URL url1 = null;
    url1 = file1.toURI().toURL();
    assertNotNull(url1);

    System.out.println(url.toString());
    Properties beanProperties = new Properties();
    beanProperties.setProperty("database.esp.url", url.toString());
    beanProperties.setProperty("database.nsfp.url", url1.toString());
    beanProperties.setProperty("download.cachelocation", "/tmp");

    ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "META-INF/db_download_beans.xml" }, false);

    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

    configurer.setProperties(beanProperties);
    configurer.setIgnoreUnresolvablePlaceholders(true);

    context.addBeanFactoryPostProcessor(configurer);    
    context.refresh();

    ThirdPartyDatabaseManager manager = (ThirdPartyDatabaseManager) context.getBean("ThirdPartyDatabaseManager");
    manager.install();
    
    ESPDao dao = (ESPDao) context.getBean("ESPDao");
    List<ESP> esps = dao.getAll();
    
    for (ESP esp : espList)
      assertTrue(esps.contains(esp));
    
    NSFPDao dao1 = (NSFPDao) context.getBean("NSFPDao");
    List<Variant> variantList = dao1.getAll();
 
    for (Variant variant : variantList) {
      Variant test = variants.get(variant.getGene().getGeneName());
      assertNotNull(test);
      test.getGene().setId(variant.getGene().getId());
      assertTrue(test.equals(variant));  
    }

    context.close();
  }
}
