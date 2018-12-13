package com.StringCalculator.pattern;

import java.util.regex.Matcher;

import com.StringCalculator.internal.util.Checks;

/**
 *
 * @author Rohit Amgaonkar
 */
public final class Patterns {

  private Patterns() {}

  public static final Pattern NEVER = new Pattern() {
    @Override
    public int match(CharSequence src, int begin, int end) {
      return MISMATCH;
    }

    @Override
    public String toString() {
      return "<>";
    }
  };

  public static final Pattern ALWAYS = new Pattern() {
    @Override
    public int match(CharSequence src, int begin, int end) {
      return 0;
    }
  };

  public static final Pattern ANY_CHAR = hasAtLeast(1);

  public static final Pattern EOF = hasExact(0);

  public static final Pattern ESCAPED = new Pattern() {
    @Override public int match(CharSequence src, int begin, int end) {
      if (begin >= (end - 1))
        return MISMATCH;
      else if (src.charAt(begin) == '\\')
        return 2;
      else
        return MISMATCH;
    }
  };

  public static final Pattern INTEGER = many1(CharPredicates.IS_DIGIT);

  public static final Pattern STRICT_DECIMAL = INTEGER.next(isChar('.').next(many(CharPredicates.IS_DIGIT)).optional());

  public static final Pattern FRACTION = isChar('.').next(INTEGER);

  public static final Pattern DECIMAL = STRICT_DECIMAL.or(FRACTION);

   public static final Pattern WORD = isChar(CharPredicates.IS_ALPHA_).next(isChar(CharPredicates.IS_ALPHA_NUMERIC_).many());

  public static final Pattern OCT_INTEGER = isChar('0').next(many(CharPredicates.range('0', '7')));

  public static final Pattern DEC_INTEGER = sequence(range('1', '9'), many(CharPredicates.IS_DIGIT));

  public static final Pattern HEX_INTEGER = string("0x").or(string("0X")).next(many1(CharPredicates.IS_HEX_DIGIT));

  public static final Pattern SCIENTIFIC_NOTATION = sequence(DECIMAL, among("eE"), among("+-").optional(), INTEGER);

  public static final Pattern REGEXP_PATTERN = getRegularExpressionPattern();

  public static final Pattern REGEXP_MODIFIERS = getModifiersPattern();

  public static Pattern hasAtLeast(final int n) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if ((begin + n) > end) return MISMATCH;
        else return n;
      }
      @Override public String toString() {
        return ".{" + n + ",}";
      }
    };
  }

  public static Pattern hasExact(final int n) {
    Checks.checkNonNegative(n, "n < 0");
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if ((begin + n) != end) return MISMATCH;
        else return n;
      }
      @Override public String toString() {
        return ".{" + n + "}";
      }
    };
  }

  public static Pattern isChar(char c) {
    return isChar(CharPredicates.isChar(c));
  }

   public static Pattern range(char c1, char c2) {
    return isChar(CharPredicates.range(c1, c2));
  }

  public static Pattern among(String chars) {
    return isChar(CharPredicates.among(chars));
  }

  public static Pattern isChar(final CharPredicate predicate) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if (begin >= end)
          return MISMATCH;
        else if (predicate.isChar(src.charAt(begin)))
          return 1;
        else
          return MISMATCH;
      }

      @Override public String toString() {
        return predicate.toString();
      }
    };
  }

  public static Pattern lineComment(String begin) {
    return string(begin).next(many(CharPredicates.notChar('\n')));
  }

  public static Pattern string(final String string) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if ((end - begin) < string.length()) return MISMATCH;
        return matchString(string, src, begin, end);
      }
      @Override public String toString() {
        return string;
      }
    };
  }

  public static Pattern stringCaseInsensitive(final String string) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        return matchStringCaseInsensitive(string, src, begin, end);
      }
      @Override public String toString() {
        return string.toUpperCase();
      }
    };
  }

  public static Pattern notString(final String string) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if (begin >= end) return MISMATCH;
        int matchedLength = matchString(string, src, begin, end);
        if ((matchedLength == MISMATCH) || (matchedLength < string.length()))
          return 1;
        else return MISMATCH;
      }
      @Override public String toString() {
        return "!(" + string + ")";
      }
    };
  }

  public static Pattern notStringCaseInsensitive(final String string) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if (begin >= end) return MISMATCH;
        if (matchStringCaseInsensitive(string, src, begin, end) == MISMATCH)
          return 1;
        else return MISMATCH;
      }
      @Override public String toString(){
        return "!(" + string.toUpperCase() + ")";
      }
    };
  }

  public static Pattern not(Pattern pattern) {
    return pattern.not();
  }

  public static Pattern and(final Pattern... patterns) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        int ret = 0;
        for (Pattern pattern : patterns) {
          int l = pattern.match(src, begin, end);
          if (l == MISMATCH) return MISMATCH;
          if (l > ret) ret = l;
        }
        return ret;
      }

      @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Pattern pattern : patterns) {
          sb.append(pattern).append(" & ");
        }
        if (sb.length() > 1) {
          sb.delete(sb.length() - 3, sb.length());
        }
        return sb.append(')').toString();
      }
    };
  }

  public static Pattern or(Pattern... patterns) {
    return new OrPattern(patterns);
  }

  static Pattern orWithoutEmpty(Pattern left, Pattern right) {
    if (right == Patterns.NEVER)
      return left;
    if (left == Patterns.NEVER)
      return right;
    return left.or(right);
  }

  static Pattern nextWithEmpty(Pattern left, Pattern right) {
    if (right == Patterns.NEVER)
      return NEVER;
    if (left == Patterns.NEVER)
      return NEVER;
    return left.next(right);
  }

  public static Pattern sequence(Pattern... patterns) {
    return new SequencePattern(patterns);
  }

  public static Pattern repeat(int n, CharPredicate predicate) {
    Checks.checkNonNegative(n, "n < 0");
    return new RepeatCharPredicatePattern(n, predicate);
  }

  @Deprecated
  public static Pattern many(int min, CharPredicate predicate) {
    return atLeast(min, predicate);
  }

  public static Pattern atLeast(final int min, final CharPredicate predicate) {
    Checks.checkMin(min);
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        int minLen = RepeatCharPredicatePattern.matchRepeat(min, predicate, src, end, begin, 0);
        if (minLen == MISMATCH) return MISMATCH;
        return matchMany(predicate, src, end, begin + minLen, minLen);
      }
      @Override public String toString() {
        return (min > 1) ? (predicate + "{" + min + ",}") : (predicate + "+");
      }
    };
  }

  public static Pattern many(final CharPredicate predicate) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        return matchMany(predicate, src, end, begin, 0);
      }
      @Override public String toString() {
        return predicate + "*";
      }
    };
  }

  @Deprecated
  public static Pattern some(int min, int max, CharPredicate predicate) {
    return times(min, max, predicate);
  }

  public static Pattern times(final int min, final int max, final CharPredicate predicate) {
    Checks.checkMinMax(min, max);
    return new Pattern() {
      @Override
      public int match(CharSequence src, int begin, int end) {
        int minLen = RepeatCharPredicatePattern.matchRepeat(min, predicate, src, end, begin, 0);
        if (minLen == MISMATCH)
          return MISMATCH;
        return matchSome(max - min, predicate, src, end, begin + minLen, minLen);
      }
    };
  }

  @Deprecated
  public static Pattern some(final int max, final CharPredicate predicate) {
    return atMost(max, predicate);
  }

  public static Pattern atMost(final int max, final CharPredicate predicate) {
    Checks.checkMax(max);
    return new Pattern() {
      @Override
      public int match(CharSequence src, int begin, int end) {
        return matchSome(max, predicate, src, end, begin, 0);
      }
    };
  }

  public static Pattern longer(Pattern p1, Pattern p2) {
    return longest(p1, p2);
  }

  public static Pattern longest(final Pattern... patterns) {
    return new Pattern() {
      @Override
      public int match(CharSequence src, int begin, int end) {
        int r = MISMATCH;
        for (Pattern pattern : patterns) {
          int l = pattern.match(src, begin, end);
          if (l > r)
            r = l;
        }
        return r;
      }
    };
  }

  /**
   * Returns a { Pattern} that tries both {@code p1} and {@code p2}, and picks the one with the shorter match
   * length. If both have the same length, {@code p1} is favored.
   */
  public static Pattern shorter(Pattern p1, Pattern p2) {
    return shortest(p1, p2);
  }

  
  public static Pattern shortest(final Pattern... patterns) {
    return new Pattern() {
      @Override
      public int match(CharSequence src, int begin, int end) {
        int r = MISMATCH;
        for (Pattern pattern : patterns) {
          final int l = pattern.match(src, begin, end);
          if (l != MISMATCH) {
            if ((r == MISMATCH) || (l < r))
              r = l;
          }
        }
        return r;
      }
    };
  }

  /** Returns a { Pattern} that matches 1 or more characters satisfying {@code predicate}. */
  public static Pattern many1(CharPredicate predicate) {
    return atLeast(1, predicate);
  }

  /** Adapts a regular expression pattern to a { Pattern}. */
  public static Pattern regex(final java.util.regex.Pattern p) {
    return new Pattern() {
      @Override
      public int match(CharSequence src, int begin, int end) {
        if (begin > end)
          return MISMATCH;
        Matcher matcher = p.matcher(src.subSequence(begin, end));
        if (matcher.lookingAt())
          return matcher.end();
        return MISMATCH;
      }
    };
  }

  /** Adapts a regular expression pattern string to a { Pattern}. */
  public static Pattern regex(String s) {
    return regex(java.util.regex.Pattern.compile(s));
  }

  static Pattern optional(Pattern pp) {
    return new OptionalPattern(pp);
  }

  private static int matchSome(int max, CharPredicate predicate, CharSequence src, int len, int from, int acc) {
    int k = Math.min(max + from, len);
    for (int i = from; i < k; i++) {
      if (!predicate.isChar(src.charAt(i)))
        return i - from + acc;
    }
    return k - from + acc;
  }

  private static Pattern getRegularExpressionPattern() {
    Pattern quote = isChar('/');
    Pattern escape = isChar('\\').next(hasAtLeast(1));
    Pattern content = or(escape, isChar(CharPredicates.notAmong("/\r\n\\")));
    return quote.next(content.many()).next(quote);
  }

  private static Pattern getModifiersPattern() {
    return isChar(CharPredicates.IS_ALPHA).many();
  }

  private static int matchMany(
      CharPredicate predicate, CharSequence src, int len, int from, int acc) {
    for (int i = from; i < len; i++) {
      if (!predicate.isChar(src.charAt(i)))
        return i - from + acc;
    }
    return len - from + acc;
  }

  private  static int matchStringCaseInsensitive(String str, CharSequence src, int begin, int end) {
    final int patternLength = str.length();
    if ((end - begin) < patternLength) return Pattern.MISMATCH;
    for (int i = 0; i < patternLength; i++) {
      final char exp = str.charAt(i);
      final char enc = src.charAt(begin + i);
      if (Character.toLowerCase(exp) != Character.toLowerCase(enc))
        return Pattern.MISMATCH;
    }
    return patternLength;
  }

  /**
   * Matches (part of) a character sequence against a pattern string.
   *
   * @param  str   the pattern string.
   * @param  src   the input sequence. Must not be null.
   * @param  begin start of index to scan characters from <code>src</code>.
   * @param  end   end of index to scan characters from <code>src</code>.
   *
   * @return the number of characters matched, or { Pattern#MISMATCH} if an unexpected character is encountered.
   */
  private static int matchString(String str, CharSequence src, int begin, int end) {
    final int patternLength = str.length();
    int i = 0;
    for (; (i < patternLength) && ((begin + i) < end); i++) {
      final char exp = str.charAt(i);
      final char enc = src.charAt(begin + i);
      if (exp != enc) return Pattern.MISMATCH;
    }
    return i;
  }
}
