package com.charite.nsfp.test.thirdpartydb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.charite.download.DownloadListener;
import com.charite.esp.dao.ESPDao;
import com.charite.esp.model.ESP;
import com.charite.model.ChromosomeId;
import com.charite.thirdpartydb.ThirdPartyDatabaseManager;

class ThirdPartyTestDownloadListener implements DownloadListener{

  @Override
  public void start(URL url, long fileSize) {
  }

  @Override
  public void progress(URL url, int percent, long seconds) {
  }

  @Override
  public void failed(String message) {
  }
  
}

public class ThirdPartyDBTest {
  private static final List<ESP> espList = new ArrayList<ESP>() {
    private static final long serialVersionUID = 1733347950107713342L;
  {
    add(new ESP(new ChromosomeId((short) 23, 200816, 'T', 'C'), (short)  1, (short) 12997, (float) 7.694083E-5));
    add(new ESP(new ChromosomeId((short) 1, 69428, 'G', 'T'), (short)  327, (short) 10343, (float) 0.031615585));
  }};
  
  @Test
  public void testDownload() throws Exception {
    File file = new File("src/test/resources/ESP6500.snps.txt.tar.gz");

    assertTrue(file.exists());
    URL url = null;
    url = file.toURI().toURL();
    assertNotNull(url);

    Properties beanProperties = new Properties();
    beanProperties.setProperty("database.esp.url", url.toString());
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
    context.close();
  }
}
