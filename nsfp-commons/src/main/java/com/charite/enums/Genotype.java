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
package com.charite.enums;

/**
 * Define of possible genotypes.
 *
 * @author Peter Robinson
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public enum Genotype {
  GENOTYPE_UNKNOWN {
    public String toString() {
      return "unknown";
    }
  },
  GENOTYPE_HOMOZYGOUS_REF {
    public String toString() {
      return "homozygous reference";
    }
  },
  GENOTYPE_HOMOZYGOUS_ALT {
    public String toString() {
      return "homozygous alt";
    }
  },
  GENOTYPE_HETEROZYGOUS {
    public String toString() {
      return "heterozygous";
    }
  };
}
