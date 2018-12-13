
package com.StringCalculator;

import java.util.function.BiFunction;

import com.StringCalculator.functors.Map3;
import com.StringCalculator.functors.Map4;
import com.StringCalculator.functors.Map5;


final class InternalFunctors {
  
  static TokenMap<Token> tokenWithSameValue(final Object value) {
    return new TokenMap<Token>() {
      @Override public Token map(Token token) {
        return (value == token.value()) ? token : null;
      }
      @Override public String toString() {
        return String.valueOf(value);
      }
    };
  }
  
  @SuppressWarnings("rawtypes")
  private static final BiFunction FIRST_OF_TWO = new BiFunction() {
    @Override public Object apply(Object first, Object b) {
      return first;
    }
    @Override public String toString() {
      return "followedBy";
    }
  };
  
  @SuppressWarnings("rawtypes")
  private static final BiFunction LAST_OF_TWO = new BiFunction() {
    @Override public Object apply(Object a, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("rawtypes")
  private static final Map3 LAST_OF_THREE = new Map3() {
    @Override public Object map(Object a, Object b, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("rawtypes")
  private static final Map4 LAST_OF_FOUR = new Map4() {
    @Override public Object map(Object a, Object b, Object c, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("rawtypes")
  private static final Map5 LAST_OF_FIVE = new Map5() {
    @Override public Object map(Object a, Object b, Object c, Object d, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("unchecked")
  static <T, B> BiFunction<T, B, T> firstOfTwo() {
    return FIRST_OF_TWO;
  }
  
  @SuppressWarnings("unchecked")
  static<A, T> BiFunction<A, T, T> lastOfTwo() {
    return LAST_OF_TWO;
  }
  
  @SuppressWarnings("unchecked")
  static<A, B, T> Map3<A, B, T, T> lastOfThree() {
    return LAST_OF_THREE;
  }
  
  @SuppressWarnings("unchecked")
  static<A, B, C, T> Map4<A, B, C, T, T> lastOfFour() {
    return LAST_OF_FOUR;
  }
  
  @SuppressWarnings("unchecked")
  static<A, B, C, D, T> Map5<A, B, C, D, T, T> lastOfFive() {
    return LAST_OF_FIVE;
  }
}
