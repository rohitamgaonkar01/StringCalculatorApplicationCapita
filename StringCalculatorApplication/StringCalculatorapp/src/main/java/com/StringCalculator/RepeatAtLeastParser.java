
package com.StringCalculator;

import java.util.Collection;
import java.util.List;

final class RepeatAtLeastParser<T> extends Parser<List<T>> {
  private final Parser<? extends T> parser;
  private final int min;
  private final ListFactory<T> listFactory;

  RepeatAtLeastParser(Parser<? extends T> parser, int min) {
    this(parser, min, ListFactory.<T>arrayListFactory());
  }

  RepeatAtLeastParser(Parser<? extends T> parser, int min, ListFactory<T> listFactory) {
    this.parser = parser;
    this.min = min;
    this.listFactory = listFactory;
  }

  @Override boolean apply(ParseContext ctxt) {
    List<T> result = listFactory.newList();
    if (!ctxt.repeat(parser, min, result))
      return false;
    if (applyMany(ctxt, result)) {
      ctxt.result = result;
      return true;
    }
    return false;
  }
  
  @Override public String toString() {
    return "atLeast";
  }

  private boolean applyMany(ParseContext ctxt, Collection<T> collection) {
    int physical = ctxt.at;
    int logical = ctxt.step;
    for (;;logical = ctxt.step) {
      if (!parser.apply(ctxt))
        return ctxt.stillThere(physical, logical);
      int at2 = ctxt.at;
      if (physical == at2) return true;
      physical = at2;
      collection.add(parser.getReturn(ctxt));
    }
  }
}