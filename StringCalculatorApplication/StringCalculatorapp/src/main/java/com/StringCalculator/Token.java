
package com.StringCalculator;

import com.StringCalculator.internal.util.Objects;

/**
 * Represents any token with a token value and the 0-based index in the source.
 * 
 * @author Rohit Amgaonkar
 */
public final class Token {
  
  private final int ind;
  private final int len;
  private final Object value;

  /**
   * @param index the starting index.
   * @param length the length of the token.
   * @param value the token value.
   */
  public Token(int index, int length, Object value) {
    this.ind = index;
    this.len = length;
    this.value = value;
  }
  
  /** Returns the length of the token. */
  public int length() {
    return len;
  }
  
  /** Returns the index of the token in the original source. */
  public int index() {
    return ind;
  }
  
  /** Returns the token value. */
  public Object value() {
    return value;
  }
  
  /** Returns the string representation of the token value. */
  @Override public String toString() {
    return String.valueOf(value);
  }
  
  @Override public int hashCode() {
    return (ind * 31 + len) * 31 + Objects.hashCode(value);
  }
  
  @Override public boolean equals(Object obj) {
    if (obj instanceof Token) {
      return equalToken((Token) obj);
    }
    return false;
  }
  
  private boolean equalToken(Token that) {
    return ind == that.ind && len == that.len && Objects.equals(value, that.value);
  }
}
