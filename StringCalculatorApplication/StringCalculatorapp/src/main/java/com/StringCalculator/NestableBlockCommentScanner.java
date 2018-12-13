
package com.StringCalculator;


final class NestableBlockCommentScanner extends Parser<Void> {
  private final Parser<?> openQuote;
  private final Parser<?> closeQuote;
  private final Parser<?> commented;
  
  NestableBlockCommentScanner(Parser<?> openQuote, Parser<?> closeQuote, Parser<?> commented) {
    this.openQuote = openQuote;
    this.closeQuote = closeQuote;
    this.commented = commented;
  }

  @Override boolean apply(final ParseContext ctxt) {
    if (!openQuote.apply(ctxt)) return false;
    for(int level = 1; level > 0;) {
      final int step = ctxt.step;
      final int at = ctxt.at;
      if (closeQuote.apply(ctxt)) {
        if (at == ctxt.at) {
          throw new IllegalStateException("closing comment scanner not consuming input.");
        }
        level--;
        continue;
      }
      if (!ctxt.stillThere(at, step)) return false;
      if (openQuote.apply(ctxt)) {
        if (at == ctxt.at) {
          throw new IllegalStateException("opening comment scanner not consuming input.");
        }
        level++;
        continue;
      }
      if (!ctxt.stillThere(at, step)) return false;
      if (commented.apply(ctxt)) {
        if (at == ctxt.at) {
          throw new IllegalStateException("commented scanner not consuming input.");
        }
        continue;
      }
      return false;
    }
    ctxt.result = null;
    return true;
  }
  
  @Override public String toString() {
    return "nestable block comment";
  }
}