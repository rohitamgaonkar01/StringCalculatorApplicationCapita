

package com.StringCalculator.internal.util;

import java.util.ArrayList;

/**
 * Internal utility to work with { java.util.List}.
 * 
 * @author Rohit Amgaonkar
 */
public final class Lists {
  
  /** Returns a new { ArrayList}. */
  public static <T> ArrayList<T> arrayList() {
    return new ArrayList<T>();
  }
  
  /** Returns a new { ArrayList} with enough capacity to hold {@code expectedElements}. */
  public static <T> ArrayList<T> arrayList(int expectedElements) {
    return new ArrayList<T>(capacity(expectedElements));
  }
  
  private static int capacity(int expectedElements) {
    return (int) Math.min(5L + expectedElements + (expectedElements / 10), Integer.MAX_VALUE);
  }
}
