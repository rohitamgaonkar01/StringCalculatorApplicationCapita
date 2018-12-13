
package com.StringCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.StringCalculator.internal.annotations.Private;
import com.StringCalculator.internal.util.Lists;


final class Operators {

  static Lexicon lexicon(final Collection<String> operatorNames) {
    final Map<String, Object> operators = new HashMap<String, Object>();
    final String[] ops = sort(operatorNames.toArray(new String[operatorNames.size()]));
    final Parser<?>[] lexers = new Parser<?>[ops.length];
    for (int i = 0; i < ops.length; i++) {
      String s = ops[i];
      Parser<?> scanner = s.length() == 1 ? Scanners.isChar(s.charAt(0)) : Scanners.string(s);
      Object value = Tokens.reserved(s);
      operators.put(s, value);
      lexers[i] = scanner.retn(value);
    }
    return new Lexicon(operators::get, Parsers.or(lexers));
  }
  
  private static final Comparator<String> LONGER_STRING_FIRST = new Comparator<String>() {
    @Override public int compare(String a, String b) {
      return b.length() - a.length();
    }
  };
  
  private static final class Suite {
    //containees are behined containers.
    final ArrayList<String> list = Lists.arrayList();
    
    Suite(String s) {
      if (s.length() > 0) list.add(s);
    }
    
    boolean add(String v) {
      if (v.length() == 0) return true;
      for (int i = list.size() - 1; i >= 0; i--) {
        String s = list.get(i);
        if (s.startsWith(v)) {
          if (s.length() == v.length()) return true;  // ignore duplicates
          list.add(i + 1, v);
          return true;
        }
      }
      return false;
    }
  }
  
  /**
   * A list of suites in the reverse order of the suites. Suite a is defined to be bigger than
   * suite b if the first element of a is longer than that of b.
   */
  private static final class Suites {
    private final ArrayList<Suite> list = Lists.arrayList();
    
    /**
     * Scans the list of suites by adding {@code v} to the first suite that claims it as a prefix.
     * If no suite claims it as prefix, it is added as a standalone { Suite} at the end of the
     * list. 
     */
    void add(String v) {
      for (Suite suite : list) {
        if (suite.add(v)) return;
      }
      list.add(new Suite(v));
    }
    
    /**
     * Collapses the names in each suite by traversing the suites in reverse order, so that smaller
     * suites are collapsed first and generally shorter operators will be placed before longer ones
     * unless it is contained by a longer operator.
     */
    String[] toArray() {
      ArrayList<String> result = Lists.arrayList();
      for (int i = list.size() - 1; i >= 0; i--) {
        Suite suite = list.get(i);
        for (String name : suite.list) {
          result.add(name);
        }
      }
      return result.toArray(new String[result.size()]);
    }
  }
  
  /**
   * Sorts {@code names} into a new array by putting short string first, unless a shorter string is
   * a prefix of a longer string, in which case, the longer string is before the prefix string.
   */
  @Private static String[] sort(String... names) {
    //short name first, unless it is fully contained in a longer name
    String[] copy = names.clone();
    Arrays.sort(copy, LONGER_STRING_FIRST);
    Suites suites = new Suites();
    for (String name : copy) {
      suites.add(name);
    }
    return suites.toArray();
  }
}
