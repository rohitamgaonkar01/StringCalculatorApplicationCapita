
package com.StringCalculator;

import static com.StringCalculator.internal.util.Checks.checkArgument;

import java.util.function.Function;

import com.StringCalculator.internal.annotations.Private;
import com.StringCalculator.internal.util.Checks;
import com.StringCalculator.internal.util.Strings;


class Lexicon {
  
  /** Maps lexical word name to token value. */
  final Function<String, Object> words;
  
  /** The scanner that recognizes any of the lexical word. */
  final Parser<?> tokenizer;
  
  Lexicon(Function<String, Object> words, Parser<?> tokenizer) {
    this.words = words;
    this.tokenizer = tokenizer;
  }
  
 
  public Parser<?> tokenizer() {
    return tokenizer;
  }
  
  
  public Parser<?> phrase(String... tokenNames) {
    Parser<?>[] wordParsers = new Parser<?>[tokenNames.length];
    for (int i = 0; i < tokenNames.length; i++) {
      wordParsers[i] = token(tokenNames[i]);
    }
    String phrase = Strings.join(" ", tokenNames);
    return Parsers.sequence(wordParsers).atomic().retn(phrase).label(phrase);
  }
  
  public Parser<Token> token(String... tokenNames) {
    if (tokenNames.length == 0) return Parsers.never();
    @SuppressWarnings("unchecked")
    Parser<Token>[] ps = new Parser[tokenNames.length];
    for(int i = 0; i < tokenNames.length; i++) {
      ps[i] = Parsers.token(InternalFunctors.tokenWithSameValue(word(tokenNames[i])));
    }
    return Parsers.or(ps);
  }
  
  public Parser<Token> token(String tokenName) {
    return Parsers.token(InternalFunctors.tokenWithSameValue(word(tokenName)));
  }

  @Private Object word(String name) {
    Object p = words.apply(name);
    Checks.checkArgument(p != null, "token %s unavailable", name);
    return p;
  }
  
  Lexicon union(Lexicon that) {
    return new Lexicon(fallback(words, that.words), Parsers.or(tokenizer, that.tokenizer));
  }

  static <F, T> Function<F, T> fallback(
      Function<F, T> function, Function<? super F, ? extends T> defaultFunction) {
    return from -> {
      T result = function.apply(from);
      return result == null ? defaultFunction.apply(from) : result;
    };
  }
}
