package com.StringCalculator.pattern;

class ManyPattern extends Pattern {

  private final Pattern pattern;

  ManyPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  static int matchMany(Pattern pattern, CharSequence src, int len, int from, int acc) {
    for (int i = from;;) {
      int l = pattern.match(src, i, len);
      if (MISMATCH == l)
        return i - from + acc;
      //we simply stop the loop when infinity is found. this may make the parser more user-friendly.
      if (l == 0)
        return i - from + acc;
      i += l;
    }
  }

  @Override
  public int match(CharSequence src, int begin, int end) {
    return matchMany(pattern, src, end, begin, 0);
  }

  @Override
  public String toString() {
    return pattern + "*";
  }

}
