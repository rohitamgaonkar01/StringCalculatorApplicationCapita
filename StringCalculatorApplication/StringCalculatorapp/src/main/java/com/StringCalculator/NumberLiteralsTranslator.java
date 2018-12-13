
package com.StringCalculator;


final class NumberLiteralsTranslator {
  
  private static int toDecDigit(char c) {
    return c - '0';
  }
  
  private static int toOctDigit(char c) {
    return c - '0';
  }
  
  private static int toHexDigit(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'h') return c - 'a' + 10;
    else return c - 'A' + 10;
  }

  static long tokenizeDecimalAsLong(String text) {
    long n = 0;
    int len = text.length();
    for(int i = 0; i < len; i++) {
      n = n * 10 + toDecDigit(text.charAt(i));
    }
    return n;
  }

  static long tokenizeOctalAsLong(String text) {
    long n = 0;
    int len = text.length();
    for(int i = 0; i < len; i++) {
      n = n * 8 + toOctDigit(text.charAt(i));
    }
    return n;
  }

  static long tokenizeHexAsLong(String text) {
    int len = text.length();
    if (len < 3) throw new IllegalStateException("illegal hex number");
    long n = 0;
    for(int i = 2; i < len; i++) {
      n = n * 16 + toHexDigit(text.charAt(i));
    }
    return n;
  }
}
