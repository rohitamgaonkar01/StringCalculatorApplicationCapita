
package com.StringCalculator.pattern;

/**
 * Evaluates a char to a boolean.
 *
 * @author Rohit Amgaonkar
 */
@FunctionalInterface
public interface CharPredicate {
  
  /** Tests whether {@code c} satisfies the predicate. */
  boolean isChar(char c);
}
