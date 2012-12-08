package com.charite.nsfp.test.nsfp;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import com.charite.enums.Genotype;
import com.charite.enums.VariantType;
import com.charite.filter.Filter;
import com.charite.nsfp.document.NSFPResultDocument;
import com.charite.nsfp.model.NSFP;
import com.charite.snv.filter.QualityFilter;
import com.charite.snv.model.SNV;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.TraxSource;

public class NSFPResultDocumentTest{
  private static final List<SNV> vcfList = new ArrayList<SNV>() {
    private static final long serialVersionUID = 684900161266848905L;
  {
    add(new SNV((short) 23, 14370    , "G"  , "A"     , VariantType.UNKNOWN     , Genotype.GENOTYPE_HOMOZYGOUS_ALT, 48, null   , null       , null    , null       , null      ));
    add(new SNV((short) 24, 17330    , "T"  , "A"     , VariantType.UNKNOWN     , Genotype.GENOTYPE_HOMOZYGOUS_REF, 49, null   , null       , null    , null       , null      ));
    add(new SNV((short) 25, 1110696  , "A"  , "G,T"   , VariantType.MISSENSE    , Genotype.GENOTYPE_UNKNOWN       , 21, null   , null       , null    , null       , null      ));
    add(new SNV((short) 20, 1234567  , "GTC", "G,GTCT", VariantType.SYNONYMOUS  , Genotype.GENOTYPE_HETEROZYGOUS  , 35, "gene1", null       , null    , null       , null      ));
    add(new SNV((short) 1 , 186050417, "A"  , "G"     , VariantType.FS_DELETION , Genotype.GENOTYPE_HOMOZYGOUS_ALT, 99, "HMCN1", "NM_031935", "exon56", "c.8678A>G", "p.E2893G"));
    add(new SNV((short) 23, 186050417, "A"  , "G"     , VariantType.STOPLOSS    , Genotype.GENOTYPE_HOMOZYGOUS_ALT, 99, "HMCN1", "NM_031935", "exon56", "c.8678A>G", null      ));
  }};
  
  @Test
  public void testResultCreation() throws Exception {
    XStream mapping = new XStream(new DomDriver());
    mapping.autodetectAnnotations(true);
    //mapping.processAnnotations(NSFPResultDocument.class);
    //mapping.processAnnotations(QualitySNVFilter.class);
    NSFPResultDocument document = new NSFPResultDocument();

    List<NSFP> nsfps = new ArrayList<>();
    nsfps.add(new NSFP(vcfList.get(0)));
    nsfps.add(new NSFP(vcfList.get(1)));
    nsfps.add(new NSFP(vcfList.get(2)));
    nsfps.add(new NSFP(vcfList.get(3)));
    nsfps.add(new NSFP(vcfList.get(4)));
    nsfps.add(new NSFP(vcfList.get(5)));

    List<String> samples = new ArrayList<>();
    samples.add("Sample1");
    samples.add("Sample2");
    samples.add("Sample3");
    samples.add("Sample4");
    
    List<Filter<SNV, SNV>> filters = new ArrayList<>();
    filters.add(new QualityFilter());
    filters.add(new QualityFilter());
    filters.add(new QualityFilter());
    
    document.setSnvFilters(filters);
    document.setSamples(samples);
    //document.setNsfpFilters(nsfpFilters)
    
    String xsl = "<?xml version=\"1.0\"?>\n"
+                "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
+                "  <xsl:template match=\"/\">\n"
+                "    <html>\n"
+                "      <body>\n"
+                "        <h1>The Exomizer: Annotate and Filter Variants</h1>\n"
+                "        <p>This is a java jdbc program that uses data from <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/21520341\">dbNSFP</a>\n"
+                "        and other sources and filters to visualize and annotate VCF data. Apply flags from the command line or \n"
+                "        Galaxy to control behaviour: path (pathogenicity filter), AR, AR, or X for inheritance filters, \n"
+                "        TG=0.05 (or similar) for Thousand Genomes frequency filter, \n"
+                "        and Q=30 for minimum genotype quality of 30 (or similar).</p>\n"
+                "        <p>Brought to you by AG Robinson, be sure to visit our <a href=\"http://compbio.charite.de\">homepage</a>.</p>\n"
+                "        <xsl:if test=\"/NSFPResult/samples\">"
+                "          <p>Samples:</p>\n"
+                "          <ul>\n" 
+                "          <xsl:for-each select=\"NSFPResult/samples/*\">\n"
+                "              <li><xsl:value-of select=\".\"/></li>\n"
+                "          </xsl:for-each>\n"
+                "          </ul>\n"
+                "        </xsl:if>\n"
+                "        <xsl:if test=\"/NSFPResult/snvFilters\">"
+                "          <ul>\n" 
+                "          <xsl:for-each select=\"NSFPResult/snvFilters/*\">\n"
+                "              <li>Total SNVs before <b><xsl:value-of select=\"filterName\"/></b> filtering <xsl:value-of select=\"elementsProcessed\"/></li>\n"
+                "              <li>Total SNVs after <b><xsl:value-of select=\"filterName\"/></b> filtering <xsl:value-of select=\"elementsFiltered\"/></li>\n"
+                "          </xsl:for-each>\n"
+                "          </ul>\n" 
+                "        </xsl:if>\n"
+                "        <table>\n"
+                "          <tr>\n"
+                "            <xsl:for-each select=\"NSFPResult/NSFPs/NSFP[1]/*\">\n"
+                "              <th><xsl:value-of select=\"name(.)\"/></th>\n"
+                "            </xsl:for-each>\n"
+                "          </tr>\n"
+                "          <xsl:for-each select=\"NSFPResult/NSFPs/NSFP\">\n"
+                "          <tr>\n"
+                "            <xsl:for-each select=\"*\">\n"
+                "              <th><xsl:value-of select=\".\"/></th>\n"
+                "            </xsl:for-each>\n"
+                "          </tr>\n"
+                "          </xsl:for-each>"
+                "        </table>\n"
+                "      </body>\n"
+                "    </html>\n"
+                "  </xsl:template>\n"
+                "</xsl:stylesheet>\n";
    
    TraxSource traxSource = new TraxSource(document, mapping);
    Writer buffer = new StringWriter();
    Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(xsl)));
    transformer.transform(traxSource, new StreamResult(buffer));
    String xml = mapping.toXML(document);
    System.out.println(buffer.toString());
    System.out.println(xml);
  }
}
