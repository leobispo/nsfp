/* Copyright (C) 2012 Leonardo Bispo de Oliveira and 
 *                    Daniele Sunaga de Oliveira
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.charite.esp.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.charite.esp.model.ESP;
import com.charite.esp.reader.ESPReader;
import com.charite.exception.InvalidFormatException;
import com.charite.exception.ParserException;
import com.charite.model.ChromosomeId;

/**
 * This class will parse a ESP file and call for each parsed line the ESPReader to consume the information.
 * 
 * @author Leonardo Bispo de Oliveira
 * 
 */
public final class ESPParser {
  private static final String DELIMITER      = " ";
  
  private static final String CHRPOS         = "chr:pos";
  private static final String ALLELES        = "Alleles";
  private static final String ALLALLELECOUNT = "AllAlleleCount";  
  
  final private ESPReader reader;
  
  /**
   * Constructor.
   * 
   * @param reader Reader to be used to consume the parsed information.
   * 
   * @throws ParserException
   * 
   */
  public ESPParser(final ESPReader reader) throws ParserException {
    if (reader == null)
      throw new ParserException("Writer cannot be a null pointer");
    
    this.reader = reader;
  }
  
  /**
   * Start the parsing the file passed as parameter.
   * 
   * @param file File to be parsed.
   * 
   * @throws FileNotFoundException
   * @throws IOException
   * @throws InvalidFormatException
   * 
   */
  public void parse(File file) throws FileNotFoundException, IOException, InvalidFormatException {
    if (!file.exists())
      throw new FileNotFoundException("File does not exists: " + file.getAbsolutePath());
    
    HashSet<String> addedElements = new HashSet<String>();
    
    final FileReader fileReader = new FileReader(file);
    final BufferedReader reader = new BufferedReader(fileReader);

    try {
      String line;
      while ((line = reader.readLine()) != null && (line.startsWith("##") || line.isEmpty())) {}
      
      if (line == null)
        throw new InvalidFormatException("File is empty");
     
      Hashtable<String, Integer> header = parseHeader(line);
     
      if (!header.containsKey(CHRPOS) || !header.containsKey(ALLELES) || !header.containsKey(ALLALLELECOUNT))
        throw new InvalidFormatException("Header does not contain all necessary fields");

      this.reader.setUp();
      while ((line = reader.readLine()) != null) {
        if (line.isEmpty() || line.startsWith("#"))
          continue;
        
        final String elements[] = line.split(DELIMITER);
        if (elements.length != header.size())
          throw new InvalidFormatException("Element does not have the same header size");
        
        final String chromosomePosition = elements[header.get(CHRPOS)];
        final String alleles            = elements[header.get(ALLELES)];
        final String allAlleleCount     = elements[header.get(ALLALLELECOUNT)];
        
        final String hash =  chromosomePosition + alleles;
        if (!addedElements.contains(hash)) {
          readElement(chromosomePosition, alleles, allAlleleCount);
          addedElements.add(hash);
        }
      }
    }
    finally {
      reader.close();
      fileReader.close();
      this.reader.end();
    }    
  }
  
  private void readElement(final String chromosomePosition, final String alleles, final String allAlleleCount) throws InvalidFormatException, ParserException {
    final String chromopos[] = chromosomePosition.split(":");
    if (chromopos.length != 2)
      throw new InvalidFormatException("Invalid Chromosome:pos element: " + chromosomePosition);
    
    Short chromosome   = chromopos[0].equals("X") ? 23 : Short.parseShort(chromopos[0]);
    Integer position   = Integer.parseInt(chromopos[1]);
    Character ref      = '.';
    Character alt      = '.';
    
    if (alleles.length() == 3 && alleles.charAt(1) == '/') {
      ref = alleles.charAt(0);
      alt = alleles.charAt(2);
    }
    
    String counts[] = allAlleleCount.split("/");
    if (counts.length < 2)
      throw new InvalidFormatException("Bad parse for counts: " + allAlleleCount);

    if (counts[0].charAt(1) != '=')
      throw new InvalidFormatException("Bad parse for counts: " + allAlleleCount);

    if (counts[1].charAt(1) != '=')
      throw new InvalidFormatException("Bad parse for counts: " + allAlleleCount);
    
    Short minor     = Short.parseShort(counts[0].substring(2));
    Short major     = Short.parseShort(counts[1].substring(2));
    Float frequency = (minor.floatValue() / major.floatValue());
    
    ChromosomeId id = new ChromosomeId(chromosome, position, ref, alt);
    ESP esp  = new ESP(id, minor, major, frequency);
    
    if (!reader.read(esp))
      throw new ParserException("Problems to read the ESP using ESPReader");
  }
  
  private Hashtable<String, Integer> parseHeader(final String headerLine) throws InvalidFormatException, ParserException {
    if (!headerLine.startsWith("#base(NCBI.37)"))
      throw new InvalidFormatException("File does not contain a header");
    
    final Hashtable<String, Integer> header = new Hashtable<String, Integer>();
    
    int i = 0;
    StringTokenizer tokenizer = new StringTokenizer(headerLine, DELIMITER);
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      if (i == 0)
        header.put(CHRPOS, i++);
      else
        header.put(token, i++);
    }
    
    return header;
  }
}
