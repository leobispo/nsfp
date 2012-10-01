package com.charite.vcf.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.input.CountingInputStream;

import com.charite.enums.Genotype;
import com.charite.enums.VariantType;
import com.charite.exception.InvalidFormatException;
import com.charite.exception.ParserException;
import com.charite.progress.ProgressListener;
import com.charite.snv.model.SNV;

public final class VCFParser {
  private static final String DELIMITER = "\t";

  private static final String CHROM  = "CHROM";
  private static final String POS    = "POS";
  private static final String ID     = "ID";
  private static final String REF    = "REF";
  private static final String ALT    = "ALT";
  private static final String QUAL   = "QUAL";
  private static final String FILTER = "FILTER";
  private static final String INFO   = "INFO";
  private static final String FORMAT = "FORMAT";

  final private List<String> samples = new ArrayList<String>();
  final private VCFReader reader;

  public VCFParser(final VCFReader reader) throws ParserException {
    if (reader == null)
      throw new ParserException("Writer cannot be a null pointer");

    this.reader = reader;
  }

  public void parse(File file, ProgressListener progress) throws FileNotFoundException, IOException, InvalidFormatException {
    if (!file.exists())
      throw new FileNotFoundException("File does not exists: " + file.getAbsolutePath());

    final List<String> rawHeader = new ArrayList<>();
    try (final CountingInputStream cntIs = new CountingInputStream(new FileInputStream(file));
         final BufferedReader reader = new BufferedReader(new InputStreamReader(cntIs));){
      String line;

      if ((line = reader.readLine()) == null || !line.startsWith("##fileformat=VCF"))
        throw new InvalidFormatException("Cannot read the file, fileformat not found");

      final String version = line.substring("##fileformat=VCF".length()).trim();
      rawHeader.add(line);

      while ((line = reader.readLine()) != null && (line.startsWith("##") || line.isEmpty()))
        rawHeader.add(line);

      if (line == null)
        throw new InvalidFormatException("File is empty");

      rawHeader.add(line);
      Hashtable<String, Integer> header = parseHeader(line);

      if (!header.containsKey(CHROM) || !header.containsKey(POS) || !header.containsKey(ID) ||
          !header.containsKey(REF) || !header.containsKey(ALT) || !header.containsKey(QUAL) ||
          !header.containsKey(FILTER) || !header.containsKey(INFO))
        throw new InvalidFormatException("Header does not contain all necessary fields");

      this.reader.setUp(version);

      long length = file.length();

      if (progress != null)
        progress.start(file.getAbsolutePath(), length);

      long stime = (new Date()).getTime();
      long seconds = 0;
      int percent  = -1;
      while ((line = reader.readLine()) != null) {
        if (line.isEmpty() || line.startsWith("#"))
          continue;

        final String elements[] = line.split(DELIMITER);
        if (elements.length != header.size())
          throw new InvalidFormatException("Element does not have the same header size");

        elements[header.get(CHROM)] = elements[header.get(CHROM)].replaceFirst("chr", "");

        final Short chromosome = elements[header.get(CHROM)].equals("X") ? 23 : elements[header.get(CHROM)].equals("Y") ? 24 :
          elements[header.get(CHROM)].equals("M") ? 25 : Short.parseShort(elements[header.get(CHROM)]);

        final Integer position = Integer.parseInt(elements[header.get(POS)]);
        final String  ref      = elements[header.get(REF)];
        final String  alt      = elements[header.get(ALT)];
        final String  info     = elements[header.get(INFO)];
        final String  format   = header.containsKey(FORMAT) ? elements[header.get(FORMAT)] : null;
        final String  sample   = (format != null && elements.length >= header.get(FORMAT) + 1) ? elements[header.get(FORMAT) + 1] : null;

        readElement(chromosome, position, ref, alt, info, format, sample);

        float readLength = cntIs.getByteCount();
        int newPercent = (int) ((readLength / length) * 100);

        long diff = (new Date()).getTime() - stime;
        long elapsedSeconds = diff / 1000;

        if (percent != newPercent || seconds < elapsedSeconds) {
          seconds = elapsedSeconds;
          percent = newPercent;
          if (progress != null)
            progress.progress(file.getAbsolutePath(), percent, seconds, (long) readLength);
        }
      }

      if (progress != null)
        progress.end(file.getAbsolutePath());
    }
    finally {
      this.reader.end(rawHeader, samples);
    }
  }

  private Hashtable<String, Integer> parseHeader(final String headerLine) throws InvalidFormatException, ParserException {
    if (!headerLine.startsWith("#CHROM"))
      throw new InvalidFormatException("File does not contain a header");

    final Hashtable<String, Integer> header = new Hashtable<>();

    boolean canBeSample = false;
    int i = 0;
    StringTokenizer tokenizer = new StringTokenizer(headerLine, DELIMITER);
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      if (i == 0)
        header.put(CHROM, i++);
      else
        header.put(token, i++);
      
      if (token.equals(INFO))
        canBeSample = true;
      
      if (canBeSample && !token.equals(FORMAT))
        samples.add(token);
    }

    return header;
  }

  private void readElement(final Short chromosome, final Integer position, final String ref, final String alt,
    final String info, final String format, final String sample) {
    if (ref.length() == 0 || ref.equals("."))
      throw new InvalidFormatException("Cannot parse " + REF + " field");

    if (alt.length() == 0 || alt.equals("."))
      throw new InvalidFormatException("Cannot parse " + ALT + " field");

    SNV vcf = new SNV(chromosome, position, ref, alt);
    parseInfos(info, vcf);

    if (format != null && sample != null)
      parseFormat(format, sample, vcf);

    if (!reader.read(vcf))
      throw new ParserException("Problems to read the SNV using VCFReader");
  }

  private void parseFormat(final String format, final String sample, SNV vcf) {
    final String formats[] = format.split(":");
    final String samples[] = sample.split(":");

    int i = 0;
    for (String f : formats) {
      if (f.equals("GT")) {
        if (samples[i].equals("0/1"))
          vcf.setGenotype(Genotype.GENOTYPE_HETEROZYGOUS);
        else if (samples[i].equals("1/1"))
          vcf.setGenotype(Genotype.GENOTYPE_HOMOZYGOUS_ALT);
        else if (samples[i].equals("0/0"))
          vcf.setGenotype(Genotype.GENOTYPE_HOMOZYGOUS_REF);
      }
      else if (f.equals("GQ"))
        vcf.setGenotypeQuality(Integer.parseInt(samples[i]));

      ++i;
    }
  }

  private void parseInfos(final String info, SNV vcf) {
    final String infos[] = info.split(";");
    for (String s : infos) {
      if (s.startsWith("EFFECT="))
        vcf.setVariantType(VariantType.fromString(s.substring("EFFECT=".length())));
      else if (s.startsWith("HGVS="))
        parseHGVS(s.substring("HGVS=".length()), vcf);
    }
  }

  private void parseHGVS(String hgvs, SNV vcf) {
    int start;
    if ((start = hgvs.indexOf("(")) > 0 ) {
      vcf.setGeneName(hgvs.substring(0, start));
      int end = hgvs.indexOf(")");

      if (end > 0) {
        hgvs = hgvs.substring(start + 1, end);
        // The following takes just the first alternative
        String list[] = hgvs.split(",");
        if (list.length > 0) {
          list = list[0].split(":");

          if (list[0].startsWith("NM_"))
            vcf.setRefSeqId(list[0]);
          if (list.length > 1 && list[1].startsWith("exon"))
            vcf.setExon(list[1]);
          if (list.length > 2 && list[2].startsWith("c."))
            vcf.setCdsMutation(list[2]);
        }
      }
    }
    else {
      String list[] = hgvs.split(":");
      if (list.length > 3) {
        vcf.setGeneName(list[0]);

        if (list[1].startsWith("NM_"))
          vcf.setRefSeqId(list[1]);
        if (list[2].startsWith("exon"))
          vcf.setExon(list[2]);
        if (list[3].startsWith("c."))
          vcf.setCdsMutation(list[3]);

        if (list.length > 4 && list[4].startsWith("p.")) {
          String aaMutation = list[4];
          if ((start = aaMutation.indexOf(",")) > 0)
            aaMutation = aaMutation.substring(0, start);

          vcf.setAaMutation(aaMutation);
        }
      }
      else
        vcf.setGeneName(hgvs);
    }
  }
}
