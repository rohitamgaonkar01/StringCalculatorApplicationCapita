
package com.StringCalculator;

import java.util.List;

/**
 * Parses a list of pattern started with a delimiter, separated and optionally
 * ended by the delimiter.
 * 
 * @author Rohit Amgaonkar
 */
final class DelimitedParser<T> extends Parser<List<T>> {
  private final Parser<T> parser;
  private final Parser<?> delim;
  private final ListFactory<T> listFactory;

  DelimitedParser(Parser<T> p, Parser<?> delim, ListFactory<T> listFactory) {
    this.parser = p;
    this.delim = delim;
    this.listFactory = listFactory;
  }

  @Override final boolean apply(final ParseContext ctxt) {
    final List<T> result = listFactory.newList();
    for (;;) {
      final int step0 = ctxt.step;
      final int at0 = ctxt.at;
      boolean r = ctxt.applyAsDelimiter(delim);
      if (!r) {
        if (!ctxt.stillThere(at0, step0)) return false;
        ctxt.result = result;
        return true;
      }
      final int step1 = ctxt.step;
      final int at1 = ctxt.at;
      r = parser.apply(ctxt);
      if (!r) {
        if (!ctxt.stillThere(at1, step1)) return false;
        ctxt.result = result;
        return true;
      }
      if (at0 == ctxt.at) { // infinite loop
        ctxt.result = result;
        return true;
      }
      result.add(parser.getReturn(ctxt));
    }
  }
  
  @Override public String toString() {
    return "delimited";
  }
}
