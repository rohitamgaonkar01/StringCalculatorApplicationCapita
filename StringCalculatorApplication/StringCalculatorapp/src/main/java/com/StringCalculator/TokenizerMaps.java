
package com.StringCalculator;

import java.util.function.Function;

import com.StringCalculator.Tokens.Fragment;
import com.StringCalculator.Tokens.ScientificNotation;
import com.StringCalculator.Tokens.Tag;

/**
 * Common { Function} implementations that maps from { String}.
 * 
 * @author Rohit Amgaonkar
 */
final class TokenizerMaps {

  /** A { Function} that returns a { Tokens.Fragment} tagged as { Tag#RESERVED}. */
  static final Function<String, Fragment> RESERVED_FRAGMENT = fragment(Tag.RESERVED);
  
  /** A { Function} that returns a { Tokens.Fragment} tagged as { Tag#IDENTIFIER}. */
  static final Function<String, Fragment> IDENTIFIER_FRAGMENT = fragment(Tag.IDENTIFIER);
  
  /** A { Function} that returns a { Tokens.Fragment} tagged as { Tag#INTEGER}. */
  static final Function<String, Fragment> INTEGER_FRAGMENT = fragment(Tag.INTEGER);
  
  /** A { Function} that returns a { Tokens.Fragment} tagged as { Tag#DECIMAL}. */
  static final Function<String, Fragment> DECIMAL_FRAGMENT = fragment(Tag.DECIMAL);
  
  /**
   * A { Function} that recognizes a scientific notation
   * and tokenizes to a { ScientificNotation}.
   */
  static final Function<String, ScientificNotation> SCIENTIFIC_NOTATION =
      new Function<String, ScientificNotation>() {
        @Override public ScientificNotation apply(String text) {
          int e = text.indexOf('e');
          if (e < 0) {
            e = text.indexOf('E');
          }
          // we know for sure the string is in expected format, so don't bother checking.
          String significand = text.substring(0, e);
          String exponent = text.substring(e + (text.charAt(e + 1) == '+' ? 2 : 1), text.length());
          return Tokens.scientificNotation(significand, exponent);
        }
        @Override public String toString() {
          return "SCIENTIFIC_NOTATION";
        }
      };
  
  /**
   * A { Function} that recognizes a string literal quoted by double quote character
   * ({@code "}) and tokenizes to a {@code String}. The backslash character ({@code \}) is
   * interpreted as escape.
   */
  static final Function<String, String> DOUBLE_QUOTE_STRING = new Function<String, String>() {
    @Override public String apply(String text) {
      return StringLiteralsTranslator.tokenizeDoubleQuote(text);
    }
    @Override public String toString() {
      return "DOUBLE_QUOTE_STRING";
    }
  };

  /**
   * A { Function} that tokenizes a SQL style string literal quoted by single quote character
   * ({@code '}) and tokenizes to a {@code String}. Two adjacent single quote characters
   * ({@code ''}) are escaped as one single quote character.
   */
  static final Function<String, String> SINGLE_QUOTE_STRING = new Function<String, String>() {
    @Override public String apply(String text) {      
      return StringLiteralsTranslator.tokenizeSingleQuote(text);
    }
    @Override public String toString() {
      return "SINGLE_QUOTE_STRING";
    }
  };
  
  /**
   * A { Function} that recognizes a character literal quoted by single quote characte
   * ({@code '} and tokenizes to a { Character}. The backslash character ({@code \}) is
   * interpreted as escape. 
   */
  static final Function<String, Character> SINGLE_QUOTE_CHAR = new Function<String, Character>() {
    @Override public Character apply(String text) {
      int len = text.length();
      if (len == 3) return text.charAt(1);
      else if (len == 4) return text.charAt(2);
      throw new IllegalStateException("illegal char");
    }
    @Override public String toString() {
      return "SINGLE_QUOTE_CHAR";
    }
  };
  
  /**
   * A { Function} that interprets the recognized character range
   * as a decimal integer and tokenizes to a { Long}.
   */
  static final Function<String, Long> DEC_AS_LONG = new Function<String, Long>() {
    @Override public Long apply(String text) {
      return NumberLiteralsTranslator.tokenizeDecimalAsLong(text);
    }
    @Override public String toString() {
      return "DEC_AS_LONG";
    }
  };
  
  /**
   * A { Function} that interprets the recognized character range
   * as a octal integer and tokenizes to a { Long}.
   */
  static final Function<String, Long> OCT_AS_LONG = new Function<String, Long>() {
    @Override public Long apply(String text) {
      return NumberLiteralsTranslator.tokenizeOctalAsLong(text);
    }
    @Override public String toString() {
      return "OCT_AS_LONG";
    }
  };
  
  /**
   * A { Function} that interprets the recognized character range
   * as a hexadecimal integer and tokenizes to a { Long}.
   */
  static final Function<String, Long> HEX_AS_LONG = new Function<String, Long>() {
    @Override public Long apply(String text) {
      return NumberLiteralsTranslator.tokenizeHexAsLong(text);
    }
    @Override public String toString() {
      return "HEX_AS_LONG";
    }
  };
  
  /**
   * Returns a map that tokenizes the recognized character range to a
   * { Tokens.Fragment} object tagged with {@code tag}.
   */
  static Function<String, Fragment> fragment(final Object tag) {
    return new Function<String, Fragment>() {
      @Override public Fragment apply(String text) {
        return Tokens.fragment(text, tag);
      }
      @Override public String toString() {
        return String.valueOf(tag);
      }
    };
  }
}
