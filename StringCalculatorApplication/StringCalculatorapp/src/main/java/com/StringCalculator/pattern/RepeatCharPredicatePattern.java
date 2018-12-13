package com.StringCalculator.pattern;

class RepeatCharPredicatePattern extends Pattern {

  private final int n;
  private final CharPredicate predicate;

  RepeatCharPredicatePattern(int n, CharPredicate predicate) {
    this.n = n;
    this.predicate = predicate;
  }

  @Override public int match(CharSequence src, int begin, int end) {
    return matchRepeat(n, predicate, src, end, begin, 0);
  }

  @Override public String toString() {
    return predicate.toString() + '{' + n + '}';
  }

  static int matchRepeat(int n, CharPredicate predicate, CharSequence src, int length, int begin, int acc) {
    int end = begin + n;
    if (end > length) return MISMATCH;
    for (int i = begin; i < end; i++) {
      if (!predicate.isChar(src.charAt(i))) return MISMATCH;
    }
    return n + acc;
  }
}
