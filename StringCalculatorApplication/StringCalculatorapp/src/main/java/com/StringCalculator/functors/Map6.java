
package com.StringCalculator.functors;

/**
 * Maps 6 objects  to an object of type {@code T}.
 * 
 * @author Rohit Amgaonkar
 * @since 3.0
 */
@FunctionalInterface
public interface Map6<A, B, C, D, E, F, T> {
  T map(A a, B b, C c, D d, E e, F f);
}
