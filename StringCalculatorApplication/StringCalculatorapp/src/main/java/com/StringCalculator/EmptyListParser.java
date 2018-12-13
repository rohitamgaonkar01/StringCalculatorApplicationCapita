
package com.StringCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser that always returns an empty mutable list.
 * 
 * @author Rohit Amgaonkar
 */
final class EmptyListParser<T> extends Parser<List<T>> {
  @SuppressWarnings("rawtypes")
  private static final EmptyListParser INSTANCE = new EmptyListParser();
  
  @SuppressWarnings("unchecked")
  static <T> Parser<List<T>> instance() {
    return INSTANCE;
  }
  
  private EmptyListParser() {}
  
  @Override boolean apply(ParseContext ctxt) {
    ctxt.result = new ArrayList<T>(0);
    return true;
  }
  
  @Override public String toString() {
    return "[]";
  }
}
