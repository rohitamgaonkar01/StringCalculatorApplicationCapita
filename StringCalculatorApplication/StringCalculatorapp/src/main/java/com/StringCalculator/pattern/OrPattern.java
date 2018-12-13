package com.StringCalculator.pattern;

class OrPattern extends Pattern {
  private final Pattern[] patterns;

  OrPattern(Pattern... patterns) {
    this.patterns = patterns;
  }

  @Override public int match(CharSequence src, int begin, int end) {
    for (Pattern pattern : patterns) {
      int l = pattern.match(src, begin, end);
      if (l != MISMATCH) return l;
    }
    return MISMATCH;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder().append('(');
    for (Pattern pattern : patterns) {
      sb.append(pattern).append(" | ");
    }
    if (sb.length() > 1) {
      sb.delete(sb.length() - 3, sb.length());
    }
    return sb.append(')').toString();
  }
}
