
package com.StringCalculator.functors;

import com.StringCalculator.internal.util.Objects;

/**
 * Immutable data holder for 2 values.
 *
 * @deprecated Prefer to using a lambda expression to convert to your own type.
 * @author Rohit Amgaonkar
 */
@Deprecated
public class Pair<A, B> {
  
  public final A a;
  public final B b;

  public Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }
  
  boolean equals(Pair<?, ?> other) {
    return Objects.equals(a, other.a) && Objects.equals(b, other.b);
  }
  
  @Override public boolean equals(Object obj) {
    if (obj instanceof Pair<?, ?>) {
      return equals((Pair<?, ?>) obj);
    }
    return false;
  }
  
  @Override public int hashCode() {
    return Objects.hashCode(a) * 31 + Objects.hashCode(b);
  }
  
  @Override public String toString() {
    return "(" + a + ", " + b + ")";
  }
}
