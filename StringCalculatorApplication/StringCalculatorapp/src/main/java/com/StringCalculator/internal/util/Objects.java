
package com.StringCalculator.internal.util;

/**
 * Utility functions for any object.
 *
 * @author Rohit Amgaonkar
 */
public final class Objects {
  /** Gets the has hcode for {@code obj}. 0 is returned if obj is null. */
  public static int hashCode(Object obj) {
    return obj == null ? 0 : obj.hashCode();
  }
  
  /**
   * Compares {@code o1} and {@code o2} for equality. Returns true if both are {@code null} or
   * {@code o1.equals(o2)}.
   */
  public static boolean equals(Object o1, Object o2) {
    return o1 == null ? o2 == null : o1.equals(o2);
  }
  
  /** Checks whether {@code obj} is one of the elements of {@code array}. */
  public static boolean in(Object obj, Object... array) {
    for (Object expected : array) {
      if (obj == expected) {
        return true;
      }
    }
    return false;
  }
}
