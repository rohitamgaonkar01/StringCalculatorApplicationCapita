
package com.StringCalculator.pattern;

class NotPattern extends Pattern {

  private final Pattern pp;

  NotPattern(Pattern pp) {
    this.pp = pp;
  }

  @Override public int match(CharSequence src, int begin, int end) {
    if (pp.match(src, begin, end) != MISMATCH) return MISMATCH;
    else return 0;
  }

  @Override public String toString() {
    return "!(" + pp.toString() + ")";
  }
}
