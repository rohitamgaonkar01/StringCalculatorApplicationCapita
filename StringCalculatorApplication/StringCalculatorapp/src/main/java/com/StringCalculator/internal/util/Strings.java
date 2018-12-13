
package com.StringCalculator.internal.util;

import static java.util.Arrays.asList;


/**
 * Internal utility for { String} operation.
 * 
 * @author Rohit Amgaonkar
 */
public final class Strings {

  /** Joins {@code objects} with {@code delim} as the delimiter. */
  public static String join(String delim, Object[] objects) {
    // Do not use varargs to prevent some silly compiler warnings.
    if (objects.length == 0) return "";
    return join(new StringBuilder(), delim, objects).toString();
  }

  /** Joins {@code objects} with {@code delim} as the delimiter. */
  public static StringBuilder join(StringBuilder builder, String delim, Object[] objects) {
    return join(builder, delim, asList(objects));
  }

  /** Joins {@code objects} with {@code delim} as the delimiter. */
  public static StringBuilder join(StringBuilder builder, String delim, Iterable<?> objects) {
    int i = 0;
    for (Object obj : objects) {
      if (i++ > 0) builder.append(delim);
      builder.append(obj);
    }
    return builder;
  }
}
