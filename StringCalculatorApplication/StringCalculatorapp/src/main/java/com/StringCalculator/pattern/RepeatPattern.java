package com.StringCalculator.pattern;

class RepeatPattern extends Pattern {
  private final int n;
  private final Pattern pattern;

  RepeatPattern(int n, Pattern pattern) {
    this.n = n;
    this.pattern = pattern;
  }

  @Override public int match(CharSequence src, int begin, int end) {
    return matchRepeat(n, pattern, src, end, begin, 0);
  }

  @Override public String toString() {
    return pattern.toString() + '{' + n + '}';
  }

  static int matchRepeat(int n, Pattern pattern, CharSequence src, int len, int from, int acc) {
    int end = from;
    for (int i = 0; i < n; i++) {
      int l = pattern.match(src, end, len);
      if (l == MISMATCH) return MISMATCH;
      end += l;
    }
    return end - from + acc;
  }
}
