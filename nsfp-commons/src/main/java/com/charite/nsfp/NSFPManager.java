package com.charite.nsfp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.charite.exception.InvalidFormatException;
import com.charite.exception.NSFPManagerException;
import com.charite.filter.Filter;
import com.charite.filter.FilterConsumer.Method;
import com.charite.filter.FilterReducer;
import com.charite.filter.FilterStopWhenFailExecutor;
import com.charite.nsfp.document.NSFPResultDocument;
import com.charite.nsfp.model.NSFP;
import com.charite.progress.ProgressListener;
import com.charite.snv.model.SNV;
import com.charite.util.Pair;
import com.charite.vcf.parser.VCFParser;
import com.charite.vcf.parser.VCFReader;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.TraxSource;

public final class NSFPManager implements VCFReader, FilterReducer<NSFP> {

  private List<Filter<NSFP, Pair<Boolean, Boolean>>> filters = null;
  private List<Filter<SNV, SNV>> snvFilters                  = null;
  private List<Filter<SNV, NSFP>> nsfpFilters                = null;

  @Autowired
  @Qualifier("DownloadListener")
  private ProgressListener listener = null;

  final NSFPResultDocument document = new NSFPResultDocument();
  
  @Autowired
  private FilterStopWhenFailExecutor<SNV, SNV> executor;
  
  public void execute(final File vcfFile, final File output, final String xsl) {
    if (executor == null)
      throw new NSFPManagerException("The filter executor is null");

    document.setVcfFile(vcfFile.getName());
    document.setInheritanceFilters(filters);
    document.setSnvFilters(snvFilters);
    document.setNsfpFilters(nsfpFilters);
    
    final VCFParser parser = new VCFParser(this);
    
    try {
      parser.parse(vcfFile, listener);
      executor.shutdown();
      
      XStream mapping = new XStream(new DomDriver());
      mapping.autodetectAnnotations(true);
      
      final TraxSource traxSource = new TraxSource(document, mapping);
      final Writer buffer = new FileWriter(output);
      
      System.out.println(mapping.toXML(document));
      Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(xsl)));
      transformer.transform(traxSource, new StreamResult(buffer));
    }
    catch (InvalidFormatException | ExecutionException | InterruptedException | IOException | TransformerFactoryConfigurationError | TransformerException e) {
      throw new NSFPManagerException("Problems to parse the file", e);
    }
  }

  public void setFilters(List<Filter<NSFP, Pair<Boolean, Boolean>>> filters) {
    this.filters = filters;
  }

  @Override
  public void setUp(String version) {
  }

  @Override
  public boolean read(SNV snv) {
    if (executor == null)
      return false;
    
    executor.consume(snv, Method.Async);
    return true;
  }

  @Override
  public void end(List<String> header, List<String> samples) {
    document.setSamples(samples);
  }

  @Override
  public void reduce(String key, ConcurrentLinkedQueue<NSFP> nsfpHints) {
    if (filters == null)
      return;
    
    boolean accept = false;
    for (Filter<NSFP, Pair<Boolean, Boolean>> filter : filters) {
      for (NSFP nsfp : nsfpHints) {
        Pair<Boolean, Boolean> ret = filter.filter(nsfp, new Integer(nsfpHints.size()));
        if (ret.getFirst() && ret.getSecond()) {
          accept = true;
          break;
        }
        
        if (!ret.getSecond()) {
          accept = false;
          break;
        }
      }
      
      if (accept) {
        document.addNSFPS(nsfpHints);
        return;
      }
    }
  }
  
  public void setSnvFilters(List<Filter<SNV, SNV>> snvFilters) {
    this.snvFilters = snvFilters;
  }

  public void setNsfpFilters(List<Filter<SNV, NSFP>> nsfpFilters) {
    this.nsfpFilters = nsfpFilters;
  }
}
