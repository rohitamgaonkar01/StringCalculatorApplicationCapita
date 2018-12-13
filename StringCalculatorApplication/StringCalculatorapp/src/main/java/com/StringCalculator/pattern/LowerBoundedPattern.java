
package com.StringCalculator.pattern;

class LowerBoundedPattern extends Pattern {
  private final int min;
  private final Pattern pattern;

  LowerBoundedPattern(int min, Pattern pattern) {
    this.min = min;
    this.pattern = pattern;
  }

  @Override public int match(CharSequence src, int begin, int end) {
    int minLen = RepeatPattern.matchRepeat(min, pattern, src, end, begin, 0);
    if (MISMATCH == minLen) return MISMATCH;
    return ManyPattern.matchMany(pattern, src, end, begin + minLen, minLen);
  }

  @Override public String toString() {
    return (min > 1) ? (pattern + "{" + min + ",}") : (pattern + "+");
  }
}
