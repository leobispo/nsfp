package com.charite.nsfp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.ThirdPartyDatabaseManager;

final class NSFPAlizerDownloadListener implements ProgressListener {
  private final List<Download> downloads = new ArrayList<Download>()
      ;
  private class Download {
    public final String url;
    public int percent  = 0;
    public long seconds = 0;

    public Download(String url, long fileSize) {
      this.url = url;
    }
  } 

  private static String readableElapsedTime(long seconds) {
    return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
  }

  private static void drawProgress(String url, int percent, long seconds) {
    StringBuilder bar = new StringBuilder();

    for (int i = 0; i < 50; ++i) {
      if(i < (percent / 2))
        bar.append("=");
      else if(i == (percent / 2))
        bar.append(">");
      else
        bar.append(" ");
    }

    System.out.println("\r" + url);
    System.out.println("\r" + readableElapsedTime(seconds) + " [" + bar.toString() + "] " + percent + "%");
  }

  @Override
  public synchronized void start(final String uid, final long fileSize) {
    downloads.add(new Download(uid, fileSize));
    System.out.println("");
    System.out.println("");
  }

  @Override
  public synchronized void progress(final String uid, final int percent, final long seconds, final long completed) {
    System.out.print("\33[" + downloads.size() * 2 + "A");
    for (Download d : downloads) {
      if (uid.equals(d.url)) {
        d.percent = percent;
        d.seconds = seconds;
      }

      drawProgress(d.url, d.percent, d.seconds);
    }
  }

  @Override
  public synchronized void failed(final String uid, final String message) {
  }

  @Override
  public void end(final String uid) {
    // TODO Auto-generated method stub
    
  }

}

public class NSFPAlizer {
  public static void main(String[] args) {


    //TODO: parse command line
    //TODO: create all filters passed by command line
    //TODO: next - Parse VCF FILE!!

    final Options options = new Options();  
    options.addOption("c", "config", true, "Configuration File.");
    options.addOption("v", "vcf", true, "Path to VCF file with mutations to be analyzed.");
    options.addOption("f", "flags", true, "Pass various flags for filtering.");
    options.addOption("o", "output", true, "Name of output file");

    final CommandLineParser cmdParser = new GnuParser();  
    CommandLine cmd;

    Properties beanProperties = new Properties();
    try {
      cmd = cmdParser.parse(options, args);

      if (cmd.hasOption('c')) {
        beanProperties.load(new FileInputStream(cmd.getOptionValue('c')));
      }
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {    

      ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "META-INF/nsfp_beans.xml" }, false);

      PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

      configurer.setProperties(beanProperties);
      configurer.setIgnoreResourceNotFound(false);
      configurer.setIgnoreUnresolvablePlaceholders(false);

      context.addBeanFactoryPostProcessor(configurer);    
      context.refresh();

      ThirdPartyDatabaseManager installManager = (ThirdPartyDatabaseManager) context.getBean("ThirdPartyDatabaseManager");
      installManager.install();

      NSFPManager nsfpManager = (NSFPManager) context.getBean("NSFPManager");
      //nsfpManager.setFilters()
      
      //TODO: first I will pass the SNV filters
      //TODO: Second I will generate the NSFP's
      //TODO: Third I will pass the NSFP filters
      
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(ExceptionUtils.getRootCauseMessage(e));
    }

  }
}
