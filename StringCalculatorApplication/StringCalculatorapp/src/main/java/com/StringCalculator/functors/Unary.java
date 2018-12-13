
package com.StringCalculator.functors;

import java.util.function.UnaryOperator;

/**
 * Represents a unary operation on the same type {@code T}.
 * 
 * <p> Implement this interface for unary operator instead of { Map} for brevity.
 *
 * @deprecated Use { java.util.function.UnaryOperator} instead.
 * @author Rohit Amgaonkar
 */
@Deprecated
public interface Unary<T> extends UnaryOperator<T>, Map<T, T> {}
