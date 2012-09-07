package com.charite.enums;

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
