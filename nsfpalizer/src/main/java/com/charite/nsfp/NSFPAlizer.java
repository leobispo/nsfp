package com.charite.nsfp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.progress.ProgressListener;
import com.charite.snv.model.SNV;

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
  }
}

public class NSFPAlizer {
  public static void main(String[] args) {
    final Options options = new Options() {
      private static final long serialVersionUID = 674039064451080584L;
    {
      addOption("c", "config", true, "Configuration File.");
      addOption("v", "vcf", true, "Path to VCF file with mutations to be analyzed.");
      addOption("f", "filters", true, "Pass various filters.");
      addOption("o", "output", true, "Name of output file");
    }};
    
    String vcfFile      = "";
    String filters      = "";
    String outputFile   = "";
    String outputFormat = "";
    final CommandLineParser cmdParser = new GnuParser();  
    CommandLine cmd;

    Properties beanProperties = new Properties();
    try {
      cmd = cmdParser.parse(options, args);

      if (cmd.hasOption('c'))
        beanProperties.load(new FileInputStream(cmd.getOptionValue('c')));
      if (cmd.hasOption('v'))
        vcfFile = cmd.getOptionValue('v');
      
      if (cmd.hasOption('f'))
        filters = cmd.getOptionValue('f');
      
      if (cmd.hasOption('o')) {
        outputFile = cmd.getOptionValue('o');
        outputFormat = outputFile.substring(outputFile.lastIndexOf('.')).toLowerCase();
      }
    }
    catch (ParseException | IOException ie) {
      printUsage(options);
    }

    if (beanProperties.isEmpty() || vcfFile.isEmpty() || filters.isEmpty() || outputFile.isEmpty() || outputFormat.isEmpty())
      printUsage(options);

    try {
      GenericApplicationContext context = new GenericApplicationContext();
      
      List<Filter<SNV, SNV>> snvFilters = new ArrayList<>();
      context.getBeanFactory().registerSingleton("SNVFilters", snvFilters);
      
      List<Filter<SNV, NSFP>> nsfpFilters = new ArrayList<>();
      context.getBeanFactory().registerSingleton("NSFPFilters", nsfpFilters);
      
      //TODO: Inheritance filters!!!
      
      (new XmlBeanDefinitionReader(context)).loadBeanDefinitions(new ClassPathResource("META-INF/nsfp_beans.xml"));
      
      PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
      configurer.setProperties(beanProperties);
      configurer.setIgnoreResourceNotFound(false);
      configurer.setIgnoreUnresolvablePlaceholders(false);
      
      context.addBeanFactoryPostProcessor(configurer);
      context.refresh();
      
      @SuppressWarnings("unchecked")
      Map<String, Filter<SNV, SNV>> snvFilterMap = (Map<String, Filter<SNV, SNV>>) context.getBean("AvailableSNVFilters");
      
      @SuppressWarnings("unchecked")
      Map<String, Filter<SNV, NSFP>> nsfpFilterMap = (Map<String, Filter<SNV, NSFP>>) context.getBean("AvailableNSFPFilters");
      
      if (!readFilters(filters, snvFilterMap, nsfpFilterMap, snvFilters, nsfpFilters))
        printFilterUsage(snvFilterMap, nsfpFilterMap);

      //ThirdPartyDatabaseManager installManager = context.getBean(ThirdPartyDatabaseManager.class);
      NSFPManager nsfpManager                  = context.getBean(NSFPManager.class);
      
      //installManager.install();
      nsfpManager.execute(new File(vcfFile), new File(outputFile), outputFormat);

      context.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(ExceptionUtils.getRootCauseMessage(e));
    }
  }
  
  private static void printFilterUsage(final Map<String, Filter<SNV, SNV>> snvFilterMap, final Map<String, Filter<SNV, NSFP>> nsfpFilterMap) {
    System.exit(1);
  }
  
  private static void printUsage(final Options options) {
    //  System.exit(1); TODO!!!
  }
  
  private static boolean readFilters(final String filters, final Map<String, Filter<SNV, SNV>> snvFilterMap, final Map<String, Filter<SNV, NSFP>> nsfpFilterMap,
    final List<Filter<SNV, SNV>> snvFilters, final List<Filter<SNV, NSFP>> nsfpFilters) {
    for (String filter : filters.split(",")) {
      String tokens[] = filter.split("=");
      final Filter<SNV, SNV> snvFilter = snvFilterMap.get(tokens[0]);
      final Filter<SNV, NSFP> nsfpFilter = nsfpFilterMap.get(tokens[0]);
      
      if (snvFilter != null)
        snvFilters.add(snvFilter);
      
      if (nsfpFilter != null)
        nsfpFilters.add(nsfpFilter);
      
      if (tokens.length > 1) {
        if (snvFilter != null && !snvFilter.setParameter(tokens[1]))
          return false;
        
        if (nsfpFilter != null && !nsfpFilter.setParameter(tokens[1]))
          return false;
      }      
    }
    
    return true;
  }
}