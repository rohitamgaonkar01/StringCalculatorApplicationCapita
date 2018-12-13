
package com.StringCalculator.functors;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Provides common implementations of { Map} interface and the variants.
 * 
 * @author Rohit Amgaonkar
 */
public final class Maps {
  
  /**
   * The { Map} that maps a { String} to { Integer} by calling
   * { Integer#valueOf(String)}.
   *
   * @deprecated Use {@code Integer::valueOf} directly.
   */
  @Deprecated
  public static final Function<String, Integer> TO_INTEGER = Integer::valueOf;

  /** The { UnaryOperator} that maps a { String} to lower case using { Locale#US}. */
  public static UnaryOperator<String> TO_LOWER_CASE = toLowerCase(Locale.US);

  /** Returns a { UnaryOperator} that maps a { String} to lower case using {@code locale}. */
  public static UnaryOperator<String> toLowerCase(final Locale locale) {
    return  new UnaryOperator<String>() {
      @Override public String apply(String s) {
        return s.toLowerCase(locale);
      }
      @Override public String toString() {
        return "toLowerCase";
      }
    };
  }

  /** The { UnaryOperator} that maps a { String} to upper case using { Locale#US}. */
  public static UnaryOperator<String> TO_UPPER_CASE = toUpperCase(Locale.US);

  /** Returns a { UnaryOperator} that maps a { String} to upper case using {@code locale}. */
  public static UnaryOperator<String> toUpperCase(Locale locale) {
    return  new UnaryOperator<String>() {
      @Override public String apply(String s) {
        return s.toUpperCase(locale);
      }
      @Override public String toString() {
        return "toUpperCase";
      }
    };
  }

  /**
   * @deprecated Use {@code String::valueOf} directly.
   */
  @Deprecated
  public static <T> Map<T, String> mapToString() {
    return String::valueOf;
  }
  
  /**
   * Returns a { Map} that maps the string representation of an enum
   * to the corresponding enum value by calling { Enum#valueOf(Class, String)}.
   */
  public static <E extends Enum<E>> Function<String, E> toEnum(Class<E> enumType) {
    return new Function<String, E>() {
      @Override public E apply(String name) {
        return Enum.valueOf(enumType, name);
      }
      @Override public String toString() {
        return "-> " + enumType.getName();
      }
    };
  }
  
  /**
   * Returns an identity map that maps parameter to itself.
   *
   * @deprecated Use { Function#identity} instead.
   */
  @Deprecated
  public static <T> UnaryOperator<T> identity() {
    return v -> v;
  }
  
  /**
   * Returns a { Map} that always maps any object to {@code v}.
   *
   * @deprecated Use {@code from -> to} directly.
   */
  @Deprecated
  public static <F, T> Function <F, T> constant(T v) {
    return from -> v;
  }
  
  /**
   * Adapts a { java.util.Map} to { Map}.
   *
   * @deprecated Use {@code Map::get} instead.
   */
  @Deprecated
  public static <K, V> Function<K, V> map(java.util.Map<K, V> m) {
    return m::get;
  }
  
  /** A { Map2} object that maps 2 values into a { Pair} object. */
  @SuppressWarnings("unchecked")
  public static <A, B> Map2<A, B, Pair<A, B>> toPair() {
    return Pair::new;
  }
  
  /** A { Map3} object that maps 3 values to a { Tuple3} object. */
  @Deprecated
  @SuppressWarnings("unchecked")
  public static <A, B, C> Map3<A, B, C, Tuple3<A, B, C>> toTuple3() {
    return Tuple3::new;
  }
  
  /** A { Map4} object that maps 4 values to a { Tuple4} object. */
  @Deprecated
  @SuppressWarnings("unchecked")
  public static <A, B, C, D> Map4<A, B, C, D, Tuple4<A, B, C, D>> toTuple4() {
    return Tuple4::new;
  }
  
  /** A { Map5} object that maps 5 values to a { Tuple5} object. */
  @SuppressWarnings("unchecked")
  @Deprecated
  public static <A, B, C, D, E> Map5<A, B, C, D, E, Tuple5<A, B, C, D, E>> toTuple5() {
    return Tuple5::new;
  }
  
  private Maps() {}
}
