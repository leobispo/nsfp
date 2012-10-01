package com.charite.nsfp.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.input.CountingInputStream;

import com.charite.exception.InvalidFormatException;
import com.charite.exception.ParserException;
import com.charite.model.ChromosomeId;
import com.charite.nsfp.model.Gene;
import com.charite.nsfp.model.Variant;
import com.charite.progress.ProgressListener;

public class NSFPParser {
  private static final String DELIMITER             = "\t";
  
  private static final String CHROM                 = "chr";
  private static final String POS                   = "pos(1-coor)";
  private static final String REF                   = "ref";
  private static final String ALT                   = "alt";
  private static final String AAREF                 = "aaref";
  private static final String AAALT                 = "aaalt";
  private static final String UNIPROT_AAPOS         = "Uniprot_aapos";
  private static final String AAPOS                 = "aapos";
  private static final String SIFT_SCORE            = "SIFT_score";
  private static final String POLYPHEN2_HVAR_SCORE  = "Polyphen2_HVAR_score";
  private static final String MUTATION_TASTER_SCORE = "MutationTaster_score";
  private static final String PHYLO_P               = "phyloP";
  private static final String TG_1000Gp1_AC         = "1000Gp1_AC";
  private static final String TG_1000Gp1_AF         = "1000Gp1_AF";
  private static final String GENENAME              = "genename";
  private static final String UNIPROT_ACC           = "Uniprot_acc";
  private static final String UNIPROT_ID            = "Uniprot_id";
  private static final String CDS_STRAND            = "cds_strand";
  private static final String ENSEMBL_GENEID        = "Ensembl_geneid";
  private static final String ENSEMBL_TRANSCRIPTID  = "Ensembl_transcriptid";

  private final static ConcurrentHashMap<String, Gene> genesRead = new ConcurrentHashMap<>();
  private final static AtomicLong geneId = new AtomicLong(0);
  
  private final NSFPReader reader;
  
  /**
   * Constructor.
   * 
   * @param reader Reader to be used to consume the parsed information.
   * 
   * @throws ParserException
   * 
   */
  public NSFPParser(final NSFPReader reader) throws ParserException {
    if (reader == null)
      throw new ParserException("Writer cannot be a null pointer");
    
    this.reader = reader;
  }
  
  /**
   * Start the parsing the file passed as parameter.
   * 
   * @param file File to be parsed.
   * @param progress Progress listener used to show the percent of completed task.
   * 
   * @throws FileNotFoundException
   * @throws IOException
   * @throws InvalidFormatException
   * 
   */
  public void parse(File file, ProgressListener progress) throws FileNotFoundException, IOException, InvalidFormatException {
    if (!file.exists())
      throw new FileNotFoundException("File does not exists: " + file.getAbsolutePath());

   

    try (final CountingInputStream cntIs = new CountingInputStream(new FileInputStream(file));
         final BufferedReader reader     = new BufferedReader(new InputStreamReader(cntIs))) {
      String line;
      while ((line = reader.readLine()) != null && (line.startsWith("##") || line.isEmpty())) {}
      
      if (line == null)
        throw new InvalidFormatException("File is empty");
     
      Hashtable<String, Integer> header = parseHeader(line);
     
      if (!header.containsKey(CHROM) || !header.containsKey(POS) || !header.containsKey(REF) || !header.containsKey(ALT)
          || !header.containsKey(AAREF) || !header.containsKey(AAALT) || !header.containsKey(UNIPROT_AAPOS) || !header.containsKey(AAPOS)
          || !header.containsKey(SIFT_SCORE) || !header.containsKey(POLYPHEN2_HVAR_SCORE) || !header.containsKey(MUTATION_TASTER_SCORE)
          || !header.containsKey(PHYLO_P) || !header.containsKey(TG_1000Gp1_AC) || !header.containsKey(TG_1000Gp1_AF) 
          || !header.containsKey(GENENAME) || !header.containsKey(UNIPROT_ACC) || !header.containsKey(UNIPROT_ID) 
          || !header.containsKey(CDS_STRAND) || !header.containsKey(ENSEMBL_GENEID) || !header.containsKey(ENSEMBL_TRANSCRIPTID)) {
        throw new InvalidFormatException("Header does not contain all necessary fields");
      }
      
      this.reader.setUp();
      
      long length = file.length();
      
      if (progress != null)
        progress.start(file.getAbsolutePath(), length);
      
      long stime = (new Date()).getTime();
      long seconds = 0;
      int percent  = -1;
      
      String currentVariant = null;
      final HashMap<Float, Variant> variantList = new HashMap<>();
 
      long cachedId = -1;     
      while ((line = reader.readLine()) != null) {
        if (line.isEmpty() || line.startsWith("#"))
          continue;
        
        final String elements[] = line.split(DELIMITER);
        if (elements.length != header.size())
          throw new InvalidFormatException("Element does not have the same header size");
       
        final String uniprotAcc         = elements[header.get(UNIPROT_ACC)].split(";")[0];
        final String uniprotId          = elements[header.get(UNIPROT_ID)].split(";")[0];
        final Character cdsStrand        = (elements[header.get(CDS_STRAND)].charAt(0) != '+' 
          && elements[header.get(CDS_STRAND)].charAt(0) != '-') ? '?' : elements[header.get(CDS_STRAND)].charAt(0);
        final String ensemblGeneId       = elements[header.get(ENSEMBL_GENEID)].split(";")[0];
        final String ensemblTranscriptId = elements[header.get(ENSEMBL_TRANSCRIPTID)].split(";")[0];
        
        final String geneName = elements[header.get(GENENAME)].split(";")[0];

        if (cachedId == -1)
          cachedId = geneId.getAndIncrement();

        Gene tmp;
        Gene gene = new Gene(cachedId, geneName, uniprotAcc, uniprotId, cdsStrand, ensemblGeneId, ensemblTranscriptId);
        if ((tmp = genesRead.putIfAbsent(geneName, gene)) == null) {
          cachedId = -1;
          if (!this.reader.read(gene))
            throw new ParserException("Problems to read the Gene using NSFPReader");
        }
        else
          gene = tmp;
                    
        final Short chromosome     = elements[header.get(CHROM)].equals("X") ? 23 : elements[header.get(CHROM)].equals("Y") ? 24 : 
          elements[header.get(CHROM)].equals("M") ? 25 : Short.parseShort(elements[header.get(CHROM)]);
        final Integer pos          = Integer.parseInt(elements[header.get(POS)]);
        final Character ref        = elements[header.get(REF)].charAt(0);
        final Character alt        = elements[header.get(ALT)].charAt(0);
        final Character aaref      = elements[header.get(AAREF)].charAt(0);
        final Character aaalt      = elements[header.get(AAALT)].charAt(0);
        final Integer uniProtAAPos = toInt(elements[header.get(UNIPROT_AAPOS)]);
        final Integer aaPos        = toInt(elements[header.get(AAPOS)]);
        final Float siftScore      = toFloat(elements[header.get(SIFT_SCORE)]);
        final Float polyphen2HVAR  = toFloat(elements[header.get(POLYPHEN2_HVAR_SCORE)]);
        final Float mutTaster      = toFloat(elements[header.get(MUTATION_TASTER_SCORE)]);
        final Float phyloP         = toFloat(elements[header.get(PHYLO_P)]);
        final Short thGenAC        = toShort(elements[header.get(TG_1000Gp1_AC)]);
        final Float thGenAF        = toFloat(elements[header.get(TG_1000Gp1_AF)]);
        
        currentVariant = readElement(chromosome, pos, ref, alt, aaref, aaalt, uniProtAAPos, aaPos, siftScore,
          polyphen2HVAR, mutTaster, phyloP, thGenAC, thGenAF, gene, currentVariant, variantList);
       
        long readLength = cntIs.getByteCount();
        int newPercent = (int) ((((double) readLength) / length) * 100);
        
        long diff = (new Date()).getTime() - stime;            
        long elapsedSeconds = diff / 1000;

        if (percent != newPercent || seconds < elapsedSeconds) {
          seconds = elapsedSeconds;
          percent = newPercent;
          if (progress != null)
            progress.progress(file.getAbsolutePath(), percent, seconds, readLength);
        }
      }
      
      if (progress != null)
        progress.end(file.getAbsolutePath());
    }
    finally {
      this.reader.end();
    }
  }
  
  private Hashtable<String, Integer> parseHeader(final String headerLine) throws InvalidFormatException, ParserException {
    if (!headerLine.startsWith("#chr"))
      throw new InvalidFormatException("File does not contain a header");
    
    final Hashtable<String, Integer> header = new Hashtable<>();
    
    int i = 0;
    StringTokenizer tokenizer = new StringTokenizer(headerLine, DELIMITER);
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      if (i == 0)
        header.put(CHROM, i++);
      else
        header.put(token, i++);
    }
    
    return header;
  }
  
  private String readElement(final Short chromosome, final Integer pos, final Character ref,
    final Character alt, final Character aaref, final Character aaalt, final Integer uniProtAAPos,
    final Integer aaPos, final Float siftScore, final Float polyphen2HVAR, final Float mutTaster,
    final Float phyloP, final Short thGenAC, final Float thGenAF, final Gene gene, final String currentVariant,
    final HashMap<Float, Variant> variantList) {

    final String variantStr = ref + Integer.toString(pos) + alt;

    if (currentVariant == null) {
      variantList.put(siftScore, new Variant(new ChromosomeId(chromosome, pos, ref, alt), aaref, aaalt, uniProtAAPos, aaPos, siftScore,
          polyphen2HVAR, mutTaster, phyloP, thGenAC, thGenAF, gene));
    }
    else if (currentVariant.equals(variantStr)) {
      variantList.put(siftScore, new Variant(new ChromosomeId(chromosome, pos, ref, alt), aaref, aaalt, uniProtAAPos, aaPos, siftScore,
        polyphen2HVAR, mutTaster, phyloP, thGenAC, thGenAF, gene));
    }
    else {
      float bestScore = -100f;
      Variant bestVariant = null;
      for (Float f : variantList.keySet()) {
        if (f > bestScore) {
          bestScore = f;
          bestVariant = variantList.get(f);
        }
      }

      if (bestVariant != null) {
        if (!reader.read(bestVariant))
          throw new ParserException("Problems to read the Variant using NSFPReader");
      }
      
      variantList.clear();
      return null;
    }
      
    return variantStr;
  }
  
  private Integer toInt(final String s) {
    try {
      return Integer.parseInt(s.split(";")[0]);
    }
    catch (NumberFormatException e) {
      return -1;
    }
  }
  
  private Float toFloat(final String s) {
    try {
      return Float.parseFloat(s.split(";")[0]);
    }
    catch (NumberFormatException e) {
      return -1f;
    }
  }
  
  private Short toShort(final String s) {
    try {
      return Short.parseShort(s.split(";")[0]);
    }
    catch (NumberFormatException e) {
      return -1;
    }
  }
}
