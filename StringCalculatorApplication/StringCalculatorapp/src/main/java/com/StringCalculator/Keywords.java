
package com.StringCalculator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.function.Function;

import com.StringCalculator.internal.annotations.Private;


final class Keywords {
  
  @Private static String[] unique(Comparator<String> c, String... names) {
    TreeSet<String> set = new TreeSet<String>(c);
    set.addAll(Arrays.asList(names));
    return set.toArray(new String[set.size()]);
  }

  static Lexicon lexicon(
      Parser<String> wordScanner, Collection<String> keywordNames,
      StringCase stringCase, final Function<String, ?> defaultMap) {
    HashMap<String, Object> map = new HashMap<String, Object>();
    for (String n : unique(stringCase, keywordNames.toArray(new String[keywordNames.size()]))) {
      Object value = Tokens.reserved(n);
      map.put(stringCase.toKey(n), value);
    }
    Function<String, Object> keywordMap = stringCase.byKey(map::get);
    return new Lexicon(keywordMap, wordScanner.map(Lexicon.fallback(keywordMap, defaultMap)));    
  }
}
