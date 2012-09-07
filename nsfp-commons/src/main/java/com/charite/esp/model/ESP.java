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
package com.charite.esp.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.charite.model.ChromosomeId;

@Entity
@Table(name="esp")
public class ESP { 
  @EmbeddedId
  private ChromosomeId id = null;
  private Short minor     = null;
  private Short major     = null;
  
  @Column(columnDefinition = "Float")
  private Float frequency = null;

  public ESP() {
    
  }
  
  public ESP(ChromosomeId id, Short minor, Short major, Float frequency) {
    this.id        = id;
    this.minor     = minor;
    this.major     = major;
    this.frequency = frequency;
  }
  
  public ChromosomeId getId() {
    return id;
  }

  public void setId(ChromosomeId id) {
    this.id = id;
  }

  public Short getMinor() {
    return minor;
  }

  public void setMinor(Short minor) {
    this.minor = minor;
  }

  public Short getMajor() {
    return major;
  }

  public void setMajor(Short major) {
    this.major = major;
  }

  public Float getFrequency() {
    return frequency;
  }

  public void setFrequency(Float frequency) {
    this.frequency = frequency;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("[+] ESP ==================================================\n")
           .append(id.toString())
           .append("Minor     : ").append(minor).append("\n")
           .append("Major     : ").append(major).append("\n")
           .append("Frequency : ").append(frequency).append("\n");
    
    return builder.toString();
  }
  
  @Override
  public int hashCode() {
    int hash = 0;
    hash = 31 * hash + hash(id);
    hash = 31 * hash + hash(minor);
    hash = 31 * hash + hash(major);
    hash = 31 * hash + hash(frequency);

    return hash;    
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof ESP) {
      ESP c = (ESP) o;
      return (equal(id, c.id) && equal(minor, c.minor) && equal(major, c.major) && equal(frequency, c.frequency));
    }
    
    return false;
  }
  
  private static int hash(Object o) {
    return o == null ? 0 : o.hashCode();
  }
  
  private static boolean equal(Object o, Object another)
  {
    return o == null ? another == null : o.equals(another);
  }
}