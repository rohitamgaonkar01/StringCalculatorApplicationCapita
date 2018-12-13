
package com.StringCalculator;

/**
 * Translates the recognized string literal to a { String}.
 * 
 * @author Rohit Amgaonkar
 */
final class StringLiteralsTranslator {
  
  private static final char escapedChar(char c) {
    switch (c) {
      case 'r':
        return '\r';
      case 'n':
        return '\n';
      case 't':
        return '\t';
      default:
        return c;
    }
  }
  
  static String tokenizeDoubleQuote(String text) {
    final int end = text.length() - 1;
    final StringBuilder buf = new StringBuilder();
    for (int i = 1; i < end; i++) {
      char c = text.charAt(i);
      if (c != '\\') {
        buf.append(c);
      }
      else {
        char c1 = text.charAt(++i);
        buf.append(escapedChar(c1));
      }
    }
    return buf.toString();
  }

  static String tokenizeSingleQuote(String text) {
    int end = text.length() - 1;
    StringBuilder buf = new StringBuilder();
    for (int i = 1; i < end; i++) {
      char c = text.charAt(i);
      if (c != '\'') {
        buf.append(c);
      }
      else {
        buf.append('\'');
        i++;
      }
    }
    return buf.toString();
  }
}
