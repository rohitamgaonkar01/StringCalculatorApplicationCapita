
package com.StringCalculator.functors;

/**
 * Maps 7 objects  to an object of type {@code T}.
 * 
 * @author Rohit Amgaonkar
 * @since 3.0
 */
@FunctionalInterface
public interface Map7<A, B, C, D, E, F, G, T> {
  T map(A a, B b, C c, D d, E e, F f, G g);
}
