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
