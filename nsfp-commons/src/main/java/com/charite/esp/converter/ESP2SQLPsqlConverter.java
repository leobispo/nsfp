package com.charite.esp.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.charite.esp.model.ESP;
import com.charite.esp.parser.ESPParser;
import com.charite.esp.reader.ESPReader;
import com.charite.exception.ConverterException;
import com.charite.exception.InvalidFormatException;
import com.charite.thirdpartydb.ThirdPartyConverter;

public class ESP2SQLPsqlConverter implements ESPReader, ThirdPartyConverter {

  @Value("${download.cachelocation}")
  private String cacheLocation = null;

  @Value("${database.psqllocation}")
  private String psql = null;
  
  @Value("${database.hostname}")
  private String hostname = null;
  
  @Value("${database.user}")
  private String user = null;
  
  @Value("${database.password}")
  private String password = null;

  @Value("${database.name}")
  private String database = null;

  
  private FileWriter out = null;
  private File file      = null;
  @Override
  public void setUp() {
    if (!new File(psql).exists())
      throw new ConverterException("Can't execute the psql command. Program does not exists.");

    try {
      file = new File(cacheLocation, new StringBuilder().append("sql_").append(UUID.randomUUID()).toString() + ".sql"); 
      out = new FileWriter(file);
    }
    catch (IOException e) {
      new ConverterException("Problems to open the temporary file to process the data", e);
    }
  }

  @Override
  public boolean read(ESP esp) {
    if (out == null || file == null)
      throw new ConverterException("Problems to open the temporary file to process the data");
    
    String str = (new StringBuilder())
      .append(esp.getId().getChromosome()).append("|")
      .append(esp.getId().getPosition()).append("|")
      .append(esp.getId().getRef()).append("|")
      .append(esp.getId().getAlt()).append("|")
      .append(esp.getMinor()).append("|")
      .append(esp.getMajor()).append("|")
      .append(esp.getFrequency()).append("\n").toString();
    
    try {
      out.write(str);
    }
    catch (IOException e) {
      return false;
    }

    return true;
  }

  @Override
  public void end() {    
    if (out == null || file == null)
      throw new ConverterException("Problems to open the temporary file to process the data");
    
    try {
      out.close();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    out = null;
    String sql = (new StringBuilder())
      .append("\\COPY esp (chromosome, position, ref, alt, minor, major, frequency) FROM '")
      .append(file.getAbsolutePath()).append("' WITH DELIMITER '|'").toString();
    
    String env[]  = { "PGPASSWORD=" + password };
    String args[] = { psql, "-U", user, "-h", hostname, "-c", sql, database};
    Runtime rt = Runtime.getRuntime();
    
    try {
      Process p = rt.exec(args, env);
      p.waitFor();
      p.destroy();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //TODO: Execute the psql passing this SQL as parameter!!
    file.delete();
  }

  @Override
  public void convert(File file) {
    ESPParser parser = new ESPParser(this);
    
    try {
      parser.parse(file);
    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InvalidFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
