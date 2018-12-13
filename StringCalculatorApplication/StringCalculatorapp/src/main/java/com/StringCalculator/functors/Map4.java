
package com.StringCalculator.functors;

/**
 * Maps 4 objects of type {@code A}, {@code B}, {@code C} and {@code D} respectively
 * to an object of type {@code T}.
 * 
 * @author Rohit Amgaonkar
 */
@FunctionalInterface
public interface Map4<A, B, C, D, T> {
  
  /** Maps {@code a}, {@code b}, {@code c} and {@code d} to the target object. */
  T map(A a, B b, C c, D d);
}
