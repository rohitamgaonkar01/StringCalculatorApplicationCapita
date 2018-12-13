
package com.StringCalculator.functors;

/**
 * Maps 3 objects of type {@code A}, {@code B} and {@code C} respectively to an object of type
 * {@code T}.
 * 
 * @author Rohit Amgaonkar
 */
@FunctionalInterface
public interface Map3<A, B, C, T> {
  
  /** Maps {@code a}, {@code b} and {@code c} to the target object. */
  T map(A a, B b, C d);
}
