package com.charite.nsfp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import com.charite.thirdpartydb.ThirdPartyDatabaseManager;
import com.charite.util.Pair;

class IndexSet<T> {  
  public int getIndex(Set<T> set, T value) {
    int result = 0;
    for (T entry : set) {
      if (entry.equals(value))
        return result;
      
      ++result;
    }
    return -1;
  }
}

/**
 * This listener is responsible to manage the progress of several data. Whenever a new class has a progress started,
 * the start method will be called. For each second or percent update, the progress method will be called. If for some
 * reason the download failed, the failed method will be called with the error message. If the class finished the the progress,
 * the end method will be called.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
final class NSFPAlizerDownloadListener implements ProgressListener {
  private final List<Download> downloads = new ArrayList<Download>();
  
  /**
   * Store information about the current download status.
   * 
   * @author Leonardo Bispo de Oliveira
   * @author Daniele Yumi Sunaga de Oliveira.
   *
   */
  private class Download {
    public final String url;
    public int percent  = 0;
    public long seconds = 0;

    /**
     * Constructor.
     * 
     * @param url URL of the file to be downloaded.
     * @param fileSize File size to be tracked.
     * 
     */
    public Download(String url, long fileSize) {
      this.url = url;
    }
  } 

  /**
   * Called when a new class with progress behavior is started.
   *
   * @param uid Unique identifier.
   * @param dataSize Total of data to be processed.
   *
   */
  @Override
  public synchronized void start(final String uid, final long fileSize) {
    downloads.add(new Download(uid, fileSize));
    System.out.println("");
    System.out.println("");
  }

  /**
   * Called when a second is changed or a percent is update.
   *
   * @param uid Unique identifier.
   * @param percent Percents completed.
   * @param seconds Elapsed time in seconds.
   * @param currentDataSize How many information is already processed.
   *
   */
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

  /**
   * Called when an error occur.
   *
   * @param uid Unique identifier.
   * @param message Error message.
   * 
   */
  @Override
  public synchronized void failed(final String uid, final String message) {
  }

  /**
   * Called when the class finish the process.
   *
   * @param uid Unique identifier.
   *
   */
  @Override
  public void end(final String uid) {
  }
  
  private static String formatElapsedTime(long seconds) {
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
    System.out.println("\r" + formatElapsedTime(seconds) + " [" + bar.toString() + "] " + percent + "%");
  }
}

/**
 * Application entry class. It will parse the command line and bootstrap the NSFP.
 * 
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Sunaga de Oliveira
 *
 */
public class NSFPAlizer {
  private enum FilterParserError { Success, DuplicateEntry, FilterNotFound, InvalidParameter, MultipleArguments };
  
  /**
   * Method Entry point.
   * 
   * @param args Command line argument list.
   * 
   */
  public static void main(String ...args) {
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
        outputFormat = outputFile.substring(outputFile.lastIndexOf('.') + 1).toLowerCase();
      }
    }
    catch (ParseException | IOException ie) {
      printUsage(options);
    }

    if (beanProperties.isEmpty() || vcfFile.isEmpty() || filters.isEmpty() || outputFile.isEmpty() || outputFormat.isEmpty())
      printUsage(options);

    try {
      final GenericApplicationContext context = new GenericApplicationContext();
      
      final List<Filter<SNV, SNV>> snvFilters = new ArrayList<>();
      context.getBeanFactory().registerSingleton("SNVFilters", snvFilters);
      
      final List<Filter<SNV, NSFP>> nsfpFilters = new ArrayList<>();
      context.getBeanFactory().registerSingleton("NSFPFilters", nsfpFilters);
      
      final List<Filter<NSFP, Pair<Boolean, Boolean>>> inheritanceFilters = new ArrayList<>();
      context.getBeanFactory().registerSingleton("InheritanceFilters", inheritanceFilters);
      
      (new XmlBeanDefinitionReader(context)).loadBeanDefinitions(new ClassPathResource("META-INF/nsfp_beans.xml"));
      
      final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
      configurer.setProperties(beanProperties);
      configurer.setIgnoreResourceNotFound(false);
      configurer.setIgnoreUnresolvablePlaceholders(false);
      
      context.addBeanFactoryPostProcessor(configurer);
      context.refresh();
      
      @SuppressWarnings("unchecked")
      final Map<String, Filter<SNV, SNV>> snvFilterMap = (Map<String, Filter<SNV, SNV>>) context.getBean("AvailableSNVFilters");
      
      @SuppressWarnings("unchecked")
      final Map<String, Filter<SNV, NSFP>> nsfpFilterMap = (Map<String, Filter<SNV, NSFP>>) context.getBean("AvailableNSFPFilters");

      @SuppressWarnings("unchecked")
      final Map<String, Filter<NSFP, Pair<Boolean, Boolean>>> inheritanceFilterMap = (Map<String, Filter<NSFP, Pair<Boolean, Boolean>>>) 
        context.getBean("AvailableInheritanceFilters");
      
      @SuppressWarnings("unchecked")
      final Map<String, String> availableOutputs = (Map<String, String>) context.getBean("AvailableOutputs");
      
      final String xsl = availableOutputs.get(outputFormat);
      if (xsl == null || xsl.isEmpty())
        printUsage(options);

      //TODO: USE THE RETURN TO HAVE A BETTER OUTPUT
      if (readFilters(filters, snvFilterMap, nsfpFilterMap, inheritanceFilterMap, snvFilters, nsfpFilters, inheritanceFilters) != FilterParserError.Success)
        printFilterUsage(snvFilterMap, nsfpFilterMap, inheritanceFilterMap);

      final ThirdPartyDatabaseManager installManager = context.getBean(ThirdPartyDatabaseManager.class);
      final NSFPManager nsfpManager                  = context.getBean(NSFPManager.class);
      
      installManager.install();
      nsfpManager.execute(new File(vcfFile), new File(outputFile), xsl);

      context.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(ExceptionUtils.getRootCauseMessage(e));
    }
  }
  
  private static void printFilterUsage(final Map<String, Filter<SNV, SNV>> snvFilterMap, final Map<String, Filter<SNV, NSFP>> nsfpFilterMap,
    final Map<String, Filter<NSFP, Pair<Boolean, Boolean>>> inheritanceFilterMap) {
    System.exit(1);
  }
  
  private static void printUsage(final Options options) {
    //System.exit(1);
  }
  
  private static FilterParserError readFilters(final String filters, final Map<String, Filter<SNV, SNV>> snvFilterMap, final Map<String, Filter<SNV, NSFP>> nsfpFilterMap,
    final Map<String, Filter<NSFP, Pair<Boolean, Boolean>>> inheritanceFilterMap, final List<Filter<SNV, SNV>> snvFilters, final List<Filter<SNV, NSFP>> nsfpFilters,
    final List<Filter<NSFP, Pair<Boolean, Boolean>>> inheritanceFilters) {
    for (String filter : filters.split(",")) {
      final String tokens[] = filter.split("=");

      final Filter<SNV, SNV>  snvFilter                            = snvFilterMap.get(tokens[0]);
      final Filter<SNV, NSFP> nsfpFilter                           = nsfpFilterMap.get(tokens[0]);
      final Filter<NSFP, Pair<Boolean, Boolean>> inheritanceFilter = inheritanceFilterMap.get(tokens[0]);
     
      if (snvFilter == null && nsfpFilter == null && inheritanceFilter == null)
        return FilterParserError.FilterNotFound;
      
      if (snvFilter != null) {
        if (snvFilters.contains(snvFilter))
          return FilterParserError.DuplicateEntry;

        if (tokens.length > 1 && !snvFilter.setParameter(tokens[1]))
          return FilterParserError.InvalidParameter;

        snvFilter.setPosition((new IndexSet<String>()).getIndex(snvFilterMap.keySet(), tokens[0]));
        snvFilters.add(snvFilter);
      }
      
      if (nsfpFilter != null) {
        if (nsfpFilters.contains(nsfpFilter))
          return FilterParserError.DuplicateEntry;
        
        if (tokens.length > 1 && !nsfpFilter.setParameter(tokens[1]))
          return FilterParserError.InvalidParameter;
        
        nsfpFilter.setPosition((new IndexSet<String>()).getIndex(nsfpFilterMap.keySet(), tokens[0]));
        nsfpFilters.add(nsfpFilter);
      }
      
      if (inheritanceFilter != null) {
        if (inheritanceFilters.size() == 1)
          return FilterParserError.MultipleArguments;
        
        inheritanceFilters.add(inheritanceFilter);
      }
    }
    
    Collections.sort(snvFilters, new Comparator<Filter<SNV, SNV>>() {
      @Override
      public int compare(Filter<SNV, SNV> o1, Filter<SNV, SNV> o2) {
        return o1.getPosition() - o2.getPosition();
      }
    });
    
    Collections.sort(nsfpFilters, new Comparator<Filter<SNV, NSFP>>() {
      @Override
      public int compare(Filter<SNV, NSFP> o1, Filter<SNV, NSFP> o2) {
        return o1.getPosition() - o2.getPosition();
      }
    });
    
    return FilterParserError.Success;
  }
}