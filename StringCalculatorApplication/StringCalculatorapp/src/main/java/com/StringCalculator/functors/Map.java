
package com.StringCalculator.functors;

import java.util.function.Function;

/**
 * Maps object of type {@code From} to an object of type {@code To}.
 *
 * @deprecated Use { java.util.function.Function} instead.
 * @author Rohit Amgaonkar
 */
@Deprecated
@FunctionalInterface
public interface Map<From, To> extends Function<From, To> {
  
  /** Maps {@code from} to the target object. */
  To map(From from);

  @Override default public To apply(From from) {
    return map(from);
  }
}
