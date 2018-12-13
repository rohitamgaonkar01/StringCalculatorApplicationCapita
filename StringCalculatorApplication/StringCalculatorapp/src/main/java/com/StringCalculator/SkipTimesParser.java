
package com.StringCalculator;

final class SkipTimesParser extends Parser<Void> {
  private final Parser<?> parser;
  private final int min;
  private final int max;

  SkipTimesParser(Parser<?> parser, int min, int max) {
    this.parser = parser;
    this.min = min;
    this.max = max;
  }

  @Override boolean apply(ParseContext ctxt) {
    if (!ctxt.repeat(parser, min)) return false;
    if (repeatAtMost(max - min, ctxt)) {
      ctxt.result = null;
      return true;
    }
    return false;
  }
  
  @Override public String toString() {
    return "skipTimes";
  }

  private boolean repeatAtMost(int times, ParseContext ctxt) {
    for (int i = 0; i < times; i++) {
      int physical = ctxt.at;
      int logical = ctxt.step;
      if (!parser.apply(ctxt))
        return ctxt.stillThere(physical, logical);
    }
    return true;
  }
}