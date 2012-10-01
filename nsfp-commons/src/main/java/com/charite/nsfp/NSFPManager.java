package com.charite.nsfp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.charite.exception.InvalidFormatException;
import com.charite.filter.Filter;
import com.charite.filter.FilterReducer;
import com.charite.filter.FilterStopWhenFailExecutor;
import com.charite.nsfp.document.NSFPResultDocument;
import com.charite.nsfp.model.NSFP;
import com.charite.progress.ProgressListener;
import com.charite.snv.model.SNV;
import com.charite.vcf.parser.VCFParser;
import com.charite.vcf.parser.VCFReader;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.TraxSource;

public final class NSFPManager implements VCFReader, FilterReducer<NSFP> {

  private List<Filter<NSFP, Boolean>> filters = null;
  
  @Autowired
  @Qualifier("DownloadListener")
  private ProgressListener listener = null;

  final NSFPResultDocument document = new NSFPResultDocument(); // THIS MUST BE INJECTED!!
  
  @Autowired
  private FilterStopWhenFailExecutor<SNV, SNV> executor;
  
  public void execute(final File vcfFile, final File output, final String xsl) {
    //TODO: VALIDATE THE OUTPUT!!!
    final VCFParser parser = new VCFParser(this);
    
    try {
      parser.parse(vcfFile, listener);
      
      XStream mapping = new XStream(new DomDriver());
      mapping.autodetectAnnotations(true);
      
      TraxSource traxSource = new TraxSource(document, mapping);
      Writer buffer = new FileWriter(output);
      Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(xsl)));
      transformer.transform(traxSource, new StreamResult(buffer));
    }
    catch (InvalidFormatException | IOException | TransformerFactoryConfigurationError | TransformerException e) {
      //THROW A NEW EXCEPTION!!
    }
  }

  @Override
  public void setUp(String version) {
  }

  @Override
  public boolean read(SNV snv) {
    executor.consume(snv);
    return true;
  }

  @Override
  public void end(List<String> header, List<String> samples) {
    document.setSamples(samples);
  }

  @Override
  public void reduce(String key, ConcurrentLinkedQueue<NSFP> nsfpHints) {
    boolean accept = false;
    for (Filter<NSFP, Boolean> filter : filters) {
      for (NSFP nsfp : nsfpHints) {
        if (filter.filter(nsfp, new Integer(nsfpHints.size()))) {
          accept = true;
        }
        if (!accept)
          return;
      }
    }
    
    document.addNSFPS(nsfpHints);
  }
}
