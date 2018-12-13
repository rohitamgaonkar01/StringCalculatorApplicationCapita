
package com.StringCalculator;

/**
 * Provides common token values.
 * 
 * @author Rohit Amgaonkar
 */
public final class Tokens {
  
  /**
   * Returns a { Fragment} tagged with {@code tag}.
   * 
   * @param text the fragment text.
   * @param tag the tag representing the fragment's semantics.
   */
  public static Fragment fragment(String text, Object tag) {
    return new Fragment(text, tag);
  }
  
  /**
   * Returns a { Fragment} tagged as { Tag#RESERVED}.
   * 
   * @param name the reserved word.
   * @return the token value.
   */
  public static Fragment reserved(String name) {
    return fragment(name, Tag.RESERVED);
  }
  
  /**
   * Returns a { Fragment} tagged as { Tag#IDENTIFIER}.
   * 
   * @param name the identifier.
   * @return the token value.
   */
  public static Fragment identifier(String name) {
    return fragment(name, Tag.IDENTIFIER);
  }
  
  /**
   * Returns a { Fragment} tagged as { Tag#DECIMAL}.
   * 
   * @param s the decimal string representation.
   * @return the token value.
   */
  public static Fragment decimalLiteral(String s) {
    return fragment(s, Tag.DECIMAL);
  }
  
  /**
   * Returns a { Fragment} tagged as { Tag#INTEGER}.
   * 
   * @param s the integer string representation.
   * @return the token value.
   */
  public static Fragment integerLiteral(String s) {
    return fragment(s, Tag.INTEGER);
  }
  
  /**
   * Returns a { ScientificNotation} with {@code significand} before the 'e' or 'E'
   * and {@code exponent} after.
   */
  public static ScientificNotation scientificNotation(String significand, String exponent) {
    return new ScientificNotation(significand, exponent);
  }
  
  /**
   * Represents a fragment tagged according to its semantics.
   * It's a convenience class so that you don't have to create many classes each for a different
   * token. Instead, you could just use {@code new fragment(text, "token1")} to uniquely identify
   * a token by the "token1" tag.
   */
  public static final class Fragment {
    private final String text;
    private final Object tag;

    /** @deprecated Use {@code Tokens.fragment()} instead. */
    @Deprecated
    public Fragment(String text, Object tag) {
      this.text = text;
      this.tag = tag;
    }
    
    /** Returns the text of the token value. */
    public String text() {
      return text;
    }
    
    /** Returns the tag of the token value. */
    public Object tag() {
      return tag;
    }
    
    boolean equalFragment(Fragment that) {
      return tag.equals(that.tag) && text.equals(that.text);
    }
    
    @Override public boolean equals(Object obj) {
      if (obj instanceof Fragment) {
        return equalFragment((Fragment) obj);
      }
      else return false;
    }

    @Override public int hashCode() {
      return tag.hashCode() * 31 + text.hashCode();
    }
    
    @Override public String toString() {
      return text;
    }
  }
  
  /**
   * Represents a scientific notation with a significand (mantissa) and an exponent. Both are
   * represented with a { String} to avoid number range issue.
   */
  public static final class ScientificNotation {
    
    /** The significand (mantissa) before the "E". */
    public final String significand;
    
    /** The exponent after the "E". */
    public final String exponent; // we leave the range check to the semantics analysis
    
    /** @deprecated Use {@code Tokens.scientificNotation()} instead. */
    @Deprecated
    public ScientificNotation(String mantissa, String exp) {
      this.significand = mantissa;
      this.exponent = exp;
    }
    
    @Override public String toString() {
      return significand + "E" + exponent;
    }
    
    @Override public boolean equals(Object obj) {
      if (obj instanceof ScientificNotation) {
        ScientificNotation that = (ScientificNotation) obj;
        return significand.equals(that.significand) && exponent.equals(that.exponent);
      }
      return false;
    }
    
    @Override public int hashCode() {
      return significand.hashCode() * 31 + exponent.hashCode();
    }
  }

  /** Pre-built { Fragment} token tags. */
  public enum Tag {
    
    /** Reserved word */
    RESERVED,
    
    /** Regular identifier */
    IDENTIFIER,
    
    /** Integral number literal */
    INTEGER,
    
    /** Decimal number literal */
    DECIMAL
  }
  
  private Tokens() {}
}

