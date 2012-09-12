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
package com.charite.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Chromosome Primary Key.
 *
 * @author Peter Robinson
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
@Embeddable
public class ChromosomeId implements Serializable {
  private static final long serialVersionUID = 8886600802154069552L;

  private Short chromosome   = null;
  private Integer position   = null;
  private Character ref      = null;
  private Character alt      = null;

  public ChromosomeId() {
  }

  public ChromosomeId(final Short chromosome, final Integer position, final Character ref, final Character alt) {
    this.chromosome = chromosome;
    this.position   = position;
    this.ref        = ref;
    this.alt        = alt;
  }

  public Short getChromosome() {
    return chromosome;
  }
  public void setChromosome(Short chromosome) {
    this.chromosome = chromosome;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Character getRef() {
    return ref;
  }

  public void setRef(Character ref) {
    this.ref = ref;
  }

  public Character getAlt() {
    return alt;
  }

  public void setAlt(Character alt) {
    this.alt = alt;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append("Chromosome : ").append(chromosome).append("\n")
           .append("Position   : ").append(position).append("\n")
           .append("Ref        : ").append(ref).append("\n")
           .append("Alt        : ").append(alt).append("\n");

    return builder.toString();
  }

  private static int hash(Object o) {
    return o == null ? 0 : o.hashCode();
  }

  public int hashCode() {
    int hash = 0;
    hash = 31 * hash + hash(chromosome);
    hash = 31 * hash + hash(position);
    hash = 31 * hash + hash(ref);
    hash = 31 * hash + hash(alt);

    return hash;
  }

  private static boolean equal(Object o, Object another)
  {
    return o == null ? another == null : o.equals(another);
  }

  public boolean equals(Object o) {
    if (o instanceof ChromosomeId) {
      ChromosomeId c = (ChromosomeId) o;
      return (equal(chromosome, c.chromosome)
           && equal(position, c.position)
           && equal(ref, c.ref)
           && equal(alt, c.alt)
      );
    }

    return false;
  }
}
