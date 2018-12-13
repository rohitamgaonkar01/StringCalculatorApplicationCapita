
package com.StringCalculator.pattern;

import com.StringCalculator.internal.util.Strings;

/**
 * Provides common { CharPredicate} implementations.
 * 
 * @author Rohit Amgaonkar
 */
public final class CharPredicates {
  
  private CharPredicates() {}
  
  /** A { CharPredicate} that always returns false. */
  public static final CharPredicate NEVER = new CharPredicate() {
    @Override public boolean isChar(char c) {return false;}
    @Override public String toString() {
      return "none";
    }
  };
  
  /** A { CharPredicate} that always returns true. */
  public static final CharPredicate ALWAYS = new CharPredicate() {
    @Override public boolean isChar(char c) {return true;}
    @Override public String toString() {
      return "any character";
    }
  };
  
  /**
   * A { CharPredicate} that returns true if the character is a digit or within the range
   * of {@code [a-f]} or {@code [A-F]}.
   */
  public static final CharPredicate IS_HEX_DIGIT = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return c>='0' && c <= '9' || c >='a' && c <='f' || c>='A' && c <= 'F';
      }
      @Override public String toString() {
        return "[0-9a-fA-F]";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if { Character#isUpperCase(char)} returns
   * true.
   */
  public static final CharPredicate IS_UPPER_CASE = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return Character.isUpperCase(c);
      }
      @Override public String toString() {
        return "uppercase";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if { Character#isLowerCase(char)} returns
   * true.
   */
  public static final CharPredicate IS_LOWER_CASE = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return Character.isLowerCase(c);
      }
      @Override public String toString() {
        return "lowercase";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if { Character#isWhitespace(char)}
   * returns true.
   */
  public static final CharPredicate IS_WHITESPACE = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return Character.isWhitespace(c);
      }
      @Override public String toString() {
        return "whitespace";
      }
    };
  
  /** A { CharPredicate} that returns true if the character is an alpha character. */
  public static final CharPredicate IS_ALPHA = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return c <='z' && c>='a' || c <='Z' && c >= 'A';
      }
      @Override public String toString() {
        return "[a-zA-Z]";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if it is an alpha character or the underscore
   * character {@code _}.
   */
  public static final CharPredicate IS_ALPHA_ = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return c == '_' || c <='z' && c>='a' || c <='Z' && c >= 'A';
      }
      @Override public String toString() {
        return "[a-zA-Z_]";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if { Character#isLetter(char)} returns
   * true.
   */
  public static final CharPredicate IS_LETTER = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return Character.isLetter(c);
      }
      @Override public String toString() {
        return "letter";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if it is an alphanumeric character, or an
   * underscore character.
   */
  public static final CharPredicate IS_ALPHA_NUMERIC = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return c>='A' && c <= 'Z' || c>='a' && c<='z' || c>='0' && c<='9';
      }
      @Override public String toString() {
        return "[0-9a-zA-Z]";
      }
    };
  
  /**
   * A { CharPredicate} that returns true if it is an alphanumeric character, or an
   * underscore character.
   */
  public static final CharPredicate IS_ALPHA_NUMERIC_ = new CharPredicate() {
      @Override public boolean isChar(char c) {
        return c == '_' || c>='A' && c <= 'Z' || c>='a' && c<='z' || c>='0' && c<='9';
      }
      @Override public String toString() {
        return "[0-9a-zA-Z_]";
      }
    };
  
  /** A { CharPredicate} that returns true if the character is equal to {@code c}. */
  public static CharPredicate isChar(final char c) {
    return new CharPredicate() {
      @Override public boolean isChar(char x) {
        return x == c;
      }
      @Override public String toString() {
        return Character.toString(c);
      }
    };
  }
  
  /** A { CharPredicate} that returns true if the character is not equal to {@code c}. */
  public static CharPredicate notChar(final char c) {
    return new CharPredicate() {
      @Override public boolean isChar(char x) {
        return x != c;
      }
      @Override public String toString() {
        return "^" + Character.toString(c);
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if the character is within the range of
   * {@code [a, b]}.
   */
  public static CharPredicate range(final char a, final char b) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return c >= a && c <= b;
      }
      @Override public String toString() {
        return "[" + a + '-' + b + "]";
      }
    };
  }
  
  /** A { CharPredicate} that returns true if the character is a digit. */
  public static final CharPredicate IS_DIGIT = range('0','9');
  
  /**
   * A { CharPredicate} that returns true if the character is not within the range of
   * {@code [a, b]}.
   */
  public static CharPredicate notRange(final char a, final char b) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return !(c >= a && c <= b);
      }
      @Override public String toString() {
        return "[^" + a + '-' + b + "]";
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if the character is equal to any character in
   * {@code chars}.
   */
  public static CharPredicate among(final String chars) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return chars.indexOf(c) >= 0;
      }
      @Override public String toString() {
        return '[' + chars + ']';
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if the character is not equal to any character
   * in {@code chars}.
   */
  public static CharPredicate notAmong(final String chars) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return chars.indexOf(c) < 0;
      }
      @Override public String toString() {
        return "^[" + chars + ']';
      }
    };
  }
  
  /** A { CharPredicate} that returns true if {@code predicate} evaluates to false. */
  public static CharPredicate not(final CharPredicate predicate) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return !predicate.isChar(c);
      }
      @Override public String toString() {
        return "^" + predicate;
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if both {@code predicate1} and
   * {@code predicate2} evaluates to true.
   */
  public static CharPredicate and(final CharPredicate predicate1, final CharPredicate predicate2) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return predicate1.isChar(c) && predicate2.isChar(c);
      }
      @Override public String toString() {
        return predicate1 + " and " + predicate2;
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if either {@code predicate1} or
   * {@code predicate2} evaluates to true.
   */
  public static CharPredicate or(final CharPredicate predicate1, final CharPredicate predicate2) {
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        return predicate1.isChar(c) || predicate2.isChar(c);
      }
      @Override public String toString() {
        return predicate1 + " or " + predicate2;
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if all {@code CharPredicate} in
   * {@code predicates} evaluate to true.
   */
  public static CharPredicate and(final CharPredicate... predicates) {
    if (predicates.length == 0)
      return ALWAYS;
    else if (predicates.length == 1) return predicates[0];
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        for(int i = 0;i < predicates.length;i++) {
          if (!predicates[i].isChar(c)) return false;
        }
        return true;
      }
      @Override public String toString() {
        return Strings.join(" and ", predicates);
      }
    };
  }
  
  /**
   * A { CharPredicate} that returns true if any {@code CharPredicate} in
   * {@code predicates} evaluates to true.
   */
  public static CharPredicate or(final CharPredicate... predicates) {
    if (predicates.length == 0)
      return NEVER;
    else if (predicates.length == 1) return predicates[0];
    return new CharPredicate() {
      @Override public boolean isChar(char c) {
        for(int i = 0;i < predicates.length;i++) {
          if (predicates[i].isChar(c)) return true;
        }
        return false;
      }
      @Override public String toString() {
        return Strings.join(" or ", predicates);
      }
    };
  }
}
