package com.charite.thirdpartydb;

/**
 * Implement this interface to have a factory of Third Party Converter.
 *
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public interface ThirdPartyConverterFactory {
  /**
   * Create a new ThirdPartyConverter.
   *
   * @return A new Converter.
   *
   */
  public ThirdPartyConverter getConverter();
}
