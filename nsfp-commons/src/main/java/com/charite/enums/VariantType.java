package com.charite.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Define of possible variants.
 *
 * @author Peter Robinson
 * @author Leonardo Bispo de Oliveira
 * @author Daniele Yumi Sunaga de Oliveira
 *
 */
public enum VariantType {
  NTERGENIC {
    public String toString() {
      return "intergenic";
    }
  },
  NCRNA_INTRONIC {
    public String toString() {
      return "ncRNA_intronic";
    }
  },
  INTRONIC {
    public String toString() {
      return "intronic";
    }
  },
  MISSENSE {
    public String toString() {
      return "missense";
    }
  },
  NONSENSE {
    public String toString() {
      return "nonsense";
    }
  },
  SYNONYMOUS {
    public String toString() {
      return "synonymous";
    }
  },
  DOWNSTREAM {
    public String toString() {
      return "downstream";
    }
  },
  NCRNA_EXONIC {
    public String toString() {
      return "ncRNA_exonic";
    }
  },
  UNKNOWN {
    public String toString() {
      return "unknown";
    }
  },
  UTR5 {
    public String toString() {
      return "UTR5";
    }
  },
  UTR3 {
    public String toString() {
      return "UTR3";
    }
  },
  STOPLOSS {
    public String toString() {
      return "stoploss";
    }
  },
  NON_FS_INSERTION {
    public String toString() {
      return "nonframeshift-insertion";
    }
  },
  NCRNA_UTR3 {
    public String toString() {
      return "ncRNA_UTR3";
    }
  },
  NCRNA_UTR5 {
    public String toString() {
      return "ncRNA_UTR5";
    }
  },
  STOPGAIN {
    public String toString() {
      return "stopgain";
    }
  },
  FS_INSERTION {
    public String toString() {
      return "frameshift-insertion";
    }
  },
  FS_DELETION {
    public String toString() {
      return "frameshift-deletion";
    }
  },
  FS_SUBSTITUTION {
    public String toString() {
      return "frameshift-substitution";
    }
  },
  NON_FS_DELETION {
    public String toString() {
      return "nonframeshift-deletion";
    }
  },
  UPSTREAM {
    public String toString() {
      return "upstream";
    }
  },
  SPLICING {
    public String toString() {
      return "splicing";
    }
  },
  NON_FS_SUBSTITUTION {
    public String toString() {
      return "nonframeshift-substitution";
    }
  },
  ncRNA_SPLICING {
    public String toString() {
      return "ncRNA_splicing";
    }
  },
  EXONIC {
    public String toString() {
      return "exonic";
    }
  };

  /** map a string to a variant type. */
  private static final Map<String, VariantType> map = new HashMap<String, VariantType>();
  static {
    for (VariantType type : VariantType.values()) {
      map.put(type.toString(), type);
    }
  }

  /**
   * This method will be used to map a string to the specific enum.
   *
   * @param entry String to be mapped.
   *
   * @return The variant type or UNKNOWN type.
   *
   */
  public static VariantType fromString(final String entry) {
    VariantType type = map.get(entry);
      if (type == null)
          return VariantType.UNKNOWN;

      return type;
  }
}
