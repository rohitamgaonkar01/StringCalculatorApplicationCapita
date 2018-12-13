
package com.StringCalculator.functors;

import java.util.function.BinaryOperator;

/**
 * Represents a binary operation on the same type {@code T}.
 * 
 * <p> Implement this interface for binary operator instead of { Map2} for brevity.
 *
 * @deprecated Use { java.util.function.BinaryOperator} instead.
 *
 * @author Rohit Amgaonkar
 */
@Deprecated
public interface Binary<T> extends BinaryOperator<T>, Map2<T, T, T> {}
