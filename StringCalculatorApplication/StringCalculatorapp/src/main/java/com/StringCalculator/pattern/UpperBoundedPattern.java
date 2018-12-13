package com.StringCalculator.pattern;

class UpperBoundedPattern extends Pattern {
  private final int max;
  private final Pattern pattern;

  UpperBoundedPattern(int max, Pattern pattern) {
    this.max = max;
    this.pattern = pattern;
  }

  @Override public int match(CharSequence src, int begin, int end) {
    return matchSome(max, pattern, src, end, begin, 0);
  }

  @Override public String toString() {
    return pattern.toString() + "{0," + max + '}';
  }

  static int matchSome(int max, Pattern pattern, CharSequence src, int len, int from, int acc) {
    int begin = from;
    for (int i = 0; i < max; i++) {
      int l = pattern.match(src, begin, len);
      if (MISMATCH == l) return begin - from + acc;
      begin += l;
    }
    return begin - from + acc;
  }
}
