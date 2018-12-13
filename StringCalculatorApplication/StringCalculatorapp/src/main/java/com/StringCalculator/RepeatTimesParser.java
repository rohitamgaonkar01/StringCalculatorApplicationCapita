
package com.StringCalculator;

import java.util.Collection;
import java.util.List;

final class RepeatTimesParser<T> extends Parser<List<T>> {
  private final Parser<? extends T> parser;
  private final int min;
  private final int max;
  private final ListFactory<T> listFactory;
  
  RepeatTimesParser(Parser<? extends T> parser, int min, int max) {
    this(parser, min, max, ListFactory.<T>arrayListFactory());
  }

  RepeatTimesParser(
      Parser<? extends T> parser, int min, int max, ListFactory<T> listFactory) {
    this.parser = parser;
    this.min = min;
    this.max = max;
    this.listFactory = listFactory;
  }

  @Override boolean apply(ParseContext ctxt) {
    List<T> result = listFactory.newList();
    if (!ctxt.repeat(parser, min, result))
      return false;
    if (repeatAtMost(max - min, result, ctxt)) {
      ctxt.result = result;
      return true;
    }
    return false;
  }
  
  @Override public String toString() {
    return "times";
  }

  private boolean repeatAtMost(int times, Collection<T> collection, ParseContext ctxt) {
    for (int i = 0; i < times; i++) {
      int physical = ctxt.at;
      int logical = ctxt.step;
      if (!parser.apply(ctxt))
        return ctxt.stillThere(physical, logical);
      collection.add(parser.getReturn(ctxt));
    }
    return true;
  }
}