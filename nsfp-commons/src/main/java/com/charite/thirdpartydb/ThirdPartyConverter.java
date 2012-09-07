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
package com.charite.thirdpartydb;

import java.io.File;

/**
 * This interface will convert a third party database file to other database format.
 * 
 * @author Leonardo Bispo de Oliveira
 *
 */
public interface ThirdPartyConverter {
  
  /**
   * Implement this method to convert a third party database file to other database format.
   * 
   * @param file Database to be converted.
   * 
   */
  public void convert(final File file);
}
