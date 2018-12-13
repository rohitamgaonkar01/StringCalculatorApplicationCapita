
package com.StringCalculator.pattern;

import com.StringCalculator.Parser;
import com.StringCalculator.Scanners;
import com.StringCalculator.internal.util.Checks;

/**
 * Encapsulates algorithm to recognize certain string pattern. When fed with a character range,
 * a { Pattern} object either fails to match, or matches with the match length returned.
 * There is no error reported on where and what exactly failed.
 * 
 * @author Rohit Amgaonkar
 */
public abstract class Pattern {
  
  /** Returned by { #match(CharSequence, int, int)} method when match fails. */
  public static final int MISMATCH = -1;
  
  /**
   * Matches character range against the pattern. The length of the range is {@code end - begin}.
   * 
   * @param src the source string.
   * @param begin the beginning index in the sequence.
   * @param end the end index of the source string (exclusive).
   * NOTE: the range is {@code [begin, end)}.
   * @return the number of characters matched. MISMATCH otherwise.
   */
  public abstract int match(CharSequence src, int begin, int end);
  
  /**
   * Returns a { Pattern} object that sequentially matches the character range against
   * {@code this} and then {@code next}. If both succeeds, the entire match length is returned.
   * 
   * @param next the next pattern to match.
   * @return the new Pattern object.
   */
  public final Pattern next(Pattern next) {
    return new SequencePattern(this, next);
  }
  
  /**
   * Returns a { Pattern} object that matches with 0 length even if {@code this} mismatches.
   */
  public final Pattern optional() {
    return new OptionalPattern(this);
  }
  
  /**
   * Returns a { Pattern} object that matches this pattern for 0 or more times.
   * The total match length is returned.
   */
  public final Pattern many() {
    return new ManyPattern(this);
  }
  
  /**
   * Returns { Pattern} object that matches this pattern for at least {@code min} times.
   * The total match length is returned.
   * 
   * @param min the minimal number of times to match.
   * @return the new Pattern object.
   * @deprecated Use { #atLeast} instead.
   */
  @Deprecated
  public final Pattern many(int min) {
    return atLeast(min);
  }
  
  /**
   * Returns { Pattern} object that matches this pattern for at least {@code min} times.
   * The total match length is returned.
   * 
   * @param min the minimal number of times to match.
   * @return the new Pattern object.
   * @since 2.2
   */
  public final Pattern atLeast(int min) {
    return new LowerBoundedPattern(Checks.checkMin(min), this);
  }
  
  /**
   * Returns a { Pattern} object that matches this pattern for 1 or more times.
   * The total match length is returned.
   */
  public final Pattern many1() {
    return atLeast(1);
  }
  
  /**
   * Returns { Pattern} object that matches this pattern for up to {@code max} times.
   * The total match length is returned.
   * 
   * @param max the maximal number of times to match.
   * @return the new Pattern object.
   * @deprecated Use { #atMost} instead.
   */
  @Deprecated
  public final Pattern some(int max) {
    return atMost(max);
  }
  
  /**
   * Returns { Pattern} object that matches this pattern for up to {@code max} times.
   * The total match length is returned.
   * 
   * @param max the maximal number of times to match.
   * @return the new Pattern object.
   * @since 2.2
   */
  public final Pattern atMost(int max) {
    return new UpperBoundedPattern(Checks.checkMax(max), this);
  }
  
  /**
   * Returns { Pattern} object that matches this pattern for at least {@code min} times
   * and up to {@code max} times. The total match length is returned.
   * 
   * @param min the minimal number of times to match.
   * @param max the maximal number of times to match.
   * @return the new Pattern object.
   * @deprecated Use { #times(int, int)} instead.
   */
  @Deprecated
  public final Pattern some(int min, int max) {
    return times(min, max);
  }
  
  /**
   * Returns { Pattern} object that matches this pattern for at least {@code min} times
   * and up to {@code max} times. The total match length is returned.
   * 
   * @param min the minimal number of times to match.
   * @param max the maximal number of times to match.
   * @return the new Pattern object.
   * @since 2.2
   */
  public final Pattern times(int min, int max) {
    return times(this, min, max);
  }
  
  /**
   * Returns a { Pattern} object that only matches if this pattern mismatches, 0 is returned
   * otherwise.
   */
  public final Pattern not() {
    return new NotPattern(this);
  }
  
  /**
   * Returns { Pattern} object that matches with match length 0 if this Pattern object matches.
   */
  public final Pattern peek() {
    return new PeekPattern(this);
  }
  
  /**
   * Returns { Pattern} object that, if this pattern matches,
   * matches the remaining input against {@code consequence} pattern, or otherwise matches against
   * {@code alternative} pattern.
   */
  public final Pattern ifelse(Pattern consequence, Pattern alternative) {
    return ifElse(this, consequence, alternative);
  }
  
  /**
   * Returns { Pattern} object that matches the input against this pattern for {@code n} times.
   * @deprecated Use { #times(int)} instead.
   */
  @Deprecated
  public final Pattern repeat(int n) {
    return times(n);
  }
  
  /**
   * Returns { Pattern} object that matches the input against this pattern for {@code n} times.
   * @since 2.2
   */
  public final Pattern times(int n) {
    return new RepeatPattern(Checks.checkNonNegative(n, "n < 0"), this);
  }
  
  /** Returns { Pattern} object that matches if either {@code this} or {@code p2} matches. */
  public final Pattern or(Pattern p2) {
    return new OrPattern(this, p2);
  }

  /**
   * Returns a scanner parser using {@code this} pattern.
   * Convenient short-hand for { Scanners#pattern}.
   *
   * @since 2.2
   */
  // ideally we want to move Pattern/Patterns into the main package. Too late for that.
  @SuppressWarnings("deprecation")
  public final Parser<Void> toScanner(String name) {
    return Scanners.pattern(this, name);
  }

  private static Pattern ifElse(
      final Pattern cond, final Pattern consequence, final Pattern alternative) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        final int conditionResult = cond.match(src, begin, end);
        if (conditionResult == MISMATCH) {
          return alternative.match(src, begin, end);
        } else {
          final int consequenceResult = consequence.match(src, begin + conditionResult, end);
          if (consequenceResult == MISMATCH)
            return MISMATCH;
          else
            return conditionResult + consequenceResult;
        }
      }
    };
  }

  private static Pattern times(final Pattern pp, final int min, final int max) {
    Checks.checkMinMax(min, max);
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        int minLen = RepeatPattern.matchRepeat(min, pp, src, end, begin, 0);
        if (MISMATCH == minLen)
          return MISMATCH;
        return UpperBoundedPattern.matchSome(max - min, pp, src, end, begin + minLen, minLen);
      }
    };
  }
}
