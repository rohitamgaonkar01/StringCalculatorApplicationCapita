
package com.StringCalculator;

final class SkipAtLeastParser extends Parser<Void> {
  private final Parser<?> parser;
  private final int min;

  SkipAtLeastParser(Parser<?> parser, int min) {
    this.parser = parser;
    this.min = min;
  }

  @Override boolean apply(ParseContext ctxt) {
    if (!ctxt.repeat(parser, min)) return false;
    if (applyMany(ctxt)) {
      ctxt.result = null;
      return true;
    }
    return false;
  }
  
  @Override public String toString() {
    return "skipAtLeast";
  }

  private boolean applyMany(ParseContext ctxt) {
    int physical = ctxt.at;
    int logical = ctxt.step;
    for (;;logical = ctxt.step) {
      if (!parser.apply(ctxt))
        return ctxt.stillThere(physical, logical);
      int at2 = ctxt.at;
      if (physical == at2) return true;
      physical = at2;
    }
  }
}