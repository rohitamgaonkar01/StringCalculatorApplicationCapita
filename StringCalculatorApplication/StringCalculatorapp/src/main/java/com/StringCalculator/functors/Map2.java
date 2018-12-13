
package com.StringCalculator.functors;

import java.util.function.BiFunction;

/**
 * Maps two objects of type {@code A} and {@code B} respectively to an object of type {@code T}.
 *
 * @deprecated Use { java.util.function.BiFunction} instead.
 * @author Rohit Amgaonkar
 */
@Deprecated
@FunctionalInterface
public interface Map2<A, B, T> extends BiFunction<A, B, T> {
  
  /** Maps {@code a} and {@code b} to the target object. */
  T map(A a, B b);

  @Override default public T apply(A a, B b) {
    return map(a, b);
  }
}
