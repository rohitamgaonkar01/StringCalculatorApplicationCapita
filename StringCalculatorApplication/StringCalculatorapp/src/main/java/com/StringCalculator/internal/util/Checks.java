
package com.StringCalculator.internal.util;

/**
 * Common facilities to check precondition, postcondition and invariants.
 *
 * @author Rohit Amgaonkar
 */
public final class Checks {

  /** Checks that {@code value} cannot be null. */
  public static <T> T checkNotNull(T value) throws NullPointerException {
    if (value == null) {
      throw new NullPointerException();
    }
    return value;
  }

  /**
   * Checks that an argument satisfies requirement.
   *
   * @param  condition the condition that has to be true
   * @param  message   the error message if {@code condition} is false
   * @param  args      the arguments to the error message
   *
   * @throws IllegalArgumentException if {@code condition} is false
   */
  public static void checkArgument(boolean condition, String message)
      throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that an argument satisfies requirement.
   *
   * @param  condition the condition that has to be true
   * @param  message   the error message if {@code condition} is false
   * @param  args      the arguments to the error message
   *
   * @throws IllegalArgumentException if {@code condition} is false
   */
  public static void checkArgument(boolean condition, String message, Object... args)
      throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks a certain state.
   *
   * @param  condition the condition of the state that has to be true
   * @param  message   the error message if {@code condition} is false
   * @param  args      the arguments to the error message
   *
   * @throws IllegalStateException if {@code condition} is false
   */
  public static void checkState(boolean condition, String message)
      throws IllegalStateException {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }

  /**
   * Checks a certain state.
   *
   * @param  condition the condition of the state that has to be true
   * @param  message   the error message if {@code condition} is false
   * @param  args      the arguments to the error message
   *
   * @throws IllegalStateException if {@code condition} is false
   */
  public static void checkState(boolean condition, String message, Object... args)
      throws IllegalStateException {
    if (!condition) {
      throw new IllegalStateException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code object} is not null.
   *
   * @param  object  the object that cannot be null
   * @param  message the error message if {@code condition} is false
   * @param  args    the arguments to the error message
   *
   * @throws IllegalStateException if {@code object} is null
   */
  public static void checkNotNullState(Object object, String message) {
    checkState(object != null, message);
  }

  /**
   * Checks that {@code object} is not null.
   *
   * @param  object  the object that cannot be null
   * @param  message the error message if {@code condition} is false
   * @param  args    the arguments to the error message
   *
   * @throws IllegalStateException if {@code object} is null
   */
  public static void checkNotNullState(Object object, String message, Object... args) {
    checkState(object != null, message, args);
  }

  /** Checks that neither {@code min} or {@code max} is negative and {@code min &lt;= max}. */
  public static void checkMinMax(int min, int max) {
    checkMin(min);
    checkMax(max);
    checkArgument(min <= max, "min > max");
  }

  /** Checks that {@code min} isn't negative. */
  public static int checkMin(int min) {
    checkNonNegative(min, "min < 0");
    return min;
  }

  /** Checks that {@code max} isn't negative. */
  public static int checkMax(int max) {
    checkNonNegative(max, "max < 0");
    return max;
  }

  /** Checks that {@code n} isn't negative. Or throws an { IllegalArgumentException} with {@code message}. */
  public static int checkNonNegative(int n, String message) {
    checkArgument(n >= 0, message);
    return n;
  }
}
