package com.charite.esp.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.charite.esp.model.ESP;
import com.charite.esp.parser.ESPParser;
import com.charite.esp.reader.ESPReader;
import com.charite.exception.ConverterException;
import com.charite.progress.ProgressListener;
import com.charite.thirdpartydb.ThirdPartyConverter;
import com.charite.thirdpartydb.ThirdPartyConverterFactory;

/**
 * This class is responsible to generate ESP2SQLPsqlConverters. It will be used by the
 * ThirdPartyDatabase to run multiple converters at the same time.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public class ESP2SQLPsqlConverterFactory implements ThirdPartyConverterFactory {

  /** Attributes injected by spring. */

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

  /**
   * Create a new ESP2SQLPsqlConverter.
   *
   * @return A new Converter.
   *
   */
  @Override
  public ThirdPartyConverter getConverter() {
    return new ESP2SQLPsqlConverter(cacheLocation, psql, hostname, user, password, database);
  }
}

/**
 * This class will receive a set o ESP entries and store it in the database using the PSQL program.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
class ESP2SQLPsqlConverter implements ESPReader, ThirdPartyConverter {

  /** Attributes injected by spring. */

  private final String cacheLocation;

  private final String psql;

  private final String hostname;

  private final String user;

  private final String password;

  private final String database;

  /** Temporary file to use the \copy postgres method. */
  private FileWriter out = null;
  private File file      = null;

  /**
   * Constructor.
   *
   * @param cacheLocation Local where the database compressed file will be stored.
   * @param psql PSQL application path.
   * @param hostname Postgres hostname.
   * @param user Postgres username.
   * @param password Postgres password.
   * @param database Postgres database.
   *
   */
  public ESP2SQLPsqlConverter(final String cacheLocation, final String psql, final String hostname, final String user, final String password, final String database) {
    this.cacheLocation = cacheLocation;
    this.psql          = psql;
    this.hostname      = hostname;
    this.user          = user;
    this.password      = password;
    this.database      = database;
  }

  /**
   * Called when the parser is starting.
   *
   */
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

  /**
   * Called for each ESP element parsed.
   *
   * @param esp ESP entry.
   *
   * @return True if the element is correctly consumed, otherwise false (False will abort the parser).
   *
   */
  @Override
  public boolean read(ESP esp) {
    if (out == null || file == null)
      throw new ConverterException("Problems to open the temporary file to process the data");

    //TODO: Must check how to Handle null values.
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

  /**
   * Called when the parser is about to finish.
   *
   */
  @Override
  public void end() {
    try {
      if (out == null || file == null)
        throw new ConverterException("Problems to open the temporary file to process the data");

      try {
        out.close();
      }
      catch (IOException e) {
        throw new ConverterException("Problems to close the Temporary file", e);
      }

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
        throw new ConverterException("Problems to run the PSQL program", e);
      }
      catch (InterruptedException e) {
      }
    }
    finally {
      file.delete();
      out = null;
    }
  }

  /**
   * Convert a third party ESP database file to SQL database.
   *
   * @param file Database to be converted.
   * @param progress Progress listener used to show the percent of completed task.
   *
   */
  @Override
  public void convert(final File file, final ProgressListener progress) {
    ESPParser parser = new ESPParser(this);

    try {
      parser.parse(file, progress);
    }
    catch (Exception e) {
      throw new ConverterException("Can't convert the ESP file.", e);
    }
  }
}
