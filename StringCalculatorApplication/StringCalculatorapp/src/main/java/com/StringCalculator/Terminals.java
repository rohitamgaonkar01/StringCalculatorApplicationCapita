
package com.StringCalculator;

import static com.StringCalculator.internal.util.Checks.checkArgument;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import com.StringCalculator.Tokens.Fragment;
import com.StringCalculator.Tokens.ScientificNotation;
import com.StringCalculator.Tokens.Tag;
import com.StringCalculator.internal.annotations.Private;
import com.StringCalculator.internal.util.Checks;
import com.StringCalculator.internal.util.Objects;
import com.StringCalculator.internal.util.Strings;

/**
 * Provides convenient API to build lexer and parsers for terminals.
 * The following example is a parser snippet for Java generic type expression such as
 * {@code List<String>}: <pre>   {@code
 *   Terminals terms = Terminals
 *       .operators("?", "<", ">", ",")
 *       .words(Scanners.IDENTIFIER)
 *       .keywords("super", "extends")
 *       .build();
 *   Parser<String> typeName = Terminals.identifier();
 *   Parser<?> wildcardWithUpperBound = terms.phrase("?", "extends");
 *   ...
 *   parser.from(terms.tokenizer(), Scanners.WHITESPACES.optional()).parse("List<String>");
 * }</pre>
 * 
 * @author Rohit Amgaonkar
 */
public final class Terminals extends Lexicon {

  /**
   * { Parser} that recognizes reserved word tokens.
   * i.e. { Tokens.Fragment} tokens tagged as { Tag#RESERVED}.
   * { Fragment#text()} is returned as parser result.
   */ 
  public static final Parser<String> RESERVED = fragment(Tag.RESERVED);
  
  /** Entry point for parser and tokenizers of character literal. */
  public static final class CharLiteral {
    
    private CharLiteral() {}
    
    /** { Parser} that recognizes { Character} tokens. */
    public static final Parser<Character> PARSER =
        Parsers.tokenType(Character.class, "character literal");
    
    /**
     * A tokenizer that parses single quoted character literal (escaped by {@code '\'}),
     * and then converts the character to a { Character} token.
     */
    public static final Parser<Character> SINGLE_QUOTE_TOKENIZER =
        Scanners.SINGLE_QUOTE_CHAR.map(TokenizerMaps.SINGLE_QUOTE_CHAR);
  }
  
  /** Entry point for parser and tokenizers of string literal. */
  public static final class StringLiteral {

    private StringLiteral() {}
    
    /** { Parser} that recognizes { String} tokens. */
    public static final Parser<String> PARSER = Parsers.tokenType(String.class, "string literal");
    
    /**
     * A tokenizer that parses double quoted string literal (escaped by {@code '\'}),
     * and transforms the quoted content by applying escape characters.
     */
    public static final Parser<String> DOUBLE_QUOTE_TOKENIZER =
        Scanners.DOUBLE_QUOTE_STRING.map(TokenizerMaps.DOUBLE_QUOTE_STRING);
    
    /**
     * A tokenizer that parses single quoted string literal (single quote is escaped with
     * another single quote), and transforms the quoted content by applying escape characters.
     */
    public static final Parser<String> SINGLE_QUOTE_TOKENIZER =
        Scanners.SINGLE_QUOTE_STRING.map(TokenizerMaps.SINGLE_QUOTE_STRING);
  }
  
  /** Entry point for parser and tokenizers of integral number literal represented as { Long}. */
  public static final class LongLiteral {

    private LongLiteral() {}
    
    /** { Parser} that recognizes { Long} tokens. */
    public static final Parser<Long> PARSER = Parsers.tokenType(Long.class, "integer literal");
    
    /**
     * A tokenizer that parses a decimal integer number (valid patterns are: {@code 1, 10, 123}),
     * and converts the string to a { Long} value.
     */
    public static final Parser<Long> DEC_TOKENIZER =
        Scanners.DEC_INTEGER.map(TokenizerMaps.DEC_AS_LONG);
    
    /**
     * A tokenizer that parses a octal integer number (valid patterns are:
     * {@code 0, 07, 017, 0371} etc.), and converts the string to a { Long} value.
     * 
     * <p> An octal number has to start with 0.
     */
    public static final Parser<Long> OCT_TOKENIZER =
        Scanners.OCT_INTEGER.map(TokenizerMaps.OCT_AS_LONG);
    
    /**
     * A tokenizer that parses a hex integer number (valid patterns are:
     * {@code 0x1, 0Xff, 0xFe1} etc.), and converts the string to a { Long} value.
     * 
     * <p> A hex number has to start with either 0x or 0X.
     */
    public static final Parser<Long> HEX_TOKENIZER =
        Scanners.HEX_INTEGER.map(TokenizerMaps.HEX_AS_LONG);
    
    /**
     * A tokenizer that parses decimal, hex, and octal numbers and converts the string to a
     * {@code Long} value.
     */
    public static final Parser<Long> TOKENIZER =
        Parsers.or(HEX_TOKENIZER, DEC_TOKENIZER, OCT_TOKENIZER);
  }
  
  /** Entry point for any arbitrary integer literal represented as a { String}. */
  public static final class IntegerLiteral {

    private IntegerLiteral() {}
    
    /**
     * { Parser} that recognizes { Tokens.Fragment} tokens tagged as { Tag#INTEGER}.
     */
    public static final Parser<String> PARSER = fragment(Tag.INTEGER);
    
    /**
     * A tokenizer that parses a integer number (valid patterns are: {@code 0, 00, 1, 10})
     * and returns a { Fragment} token tagged as { Tag#INTEGER}.
     */
    public static final Parser<Fragment> TOKENIZER =
        Scanners.INTEGER.map(TokenizerMaps.INTEGER_FRAGMENT);
  }
  
  /** Entry point for parser and tokenizers of decimal number literal represented as { String}.*/
  public static final class DecimalLiteral {

    private DecimalLiteral() {}
    
    /**
     * { Parser} that recognizes { Tokens.Fragment} tokens tagged as { Tag#DECIMAL}.
     */
    public static final Parser<String> PARSER = fragment(Tag.DECIMAL);
    
    /**
     * A tokenizer that parses a decimal number (valid patterns are: {@code 1, 2.3, 00, 0., .23})
     * and returns a { Fragment} token tagged as { Tag#DECIMAL}.
     */
    public static final Parser<Fragment> TOKENIZER =
        Scanners.DECIMAL.map(TokenizerMaps.DECIMAL_FRAGMENT);
  }
  
  /** Entry point for parser and tokenizers of scientific notation literal. */
  public static final class ScientificNumberLiteral {
    
    private ScientificNumberLiteral() {}
    
    /** { Parser} that recognies { ScientificNotation} tokens. */
    public static final Parser<ScientificNotation> PARSER =
        Parsers.tokenType(ScientificNotation.class, "scientific number literal");
    
    /**
     * A tokenizer that parses a scientific notation and converts the string to a
     * { ScientificNotation} value.
     */
    public static final Parser<ScientificNotation> TOKENIZER =
        Scanners.SCIENTIFIC_NOTATION.map(TokenizerMaps.SCIENTIFIC_NOTATION);
  }
  
  /** Entry point for parser and tokenizers of regular identifier. */
  public static final class Identifier {

    private Identifier() {}
    
    /**
     * { Parser} that recognizes identifier tokens.
     * i.e. { Tokens.Fragment} tokens tagged as { Tag#IDENTIFIER}.
     * { Fragment#text()} is returned as parser result.
     */ 
    public static final Parser<String> PARSER = fragment(Tag.IDENTIFIER);
    
    /**
     * A tokenizer that parses any identifier and returns a { Fragment} token tagged as
     * { Tag#IDENTIFIER}.
     * 
     * <p> An identifier starts with an alphabetic character or underscore,
     * and is followed by 0 or more alphanumeric characters or underscore.
     */
    public static final Parser<Fragment> TOKENIZER =
        Scanners.IDENTIFIER.map(TokenizerMaps.IDENTIFIER_FRAGMENT);
  }
  
  private Terminals(Lexicon lexicon) {
    super(lexicon.words, lexicon.tokenizer);
  }

  /**
   * Returns a { Terminals} object for lexing and parsing the operators with names specified in
   * {@code ops}, and for lexing and parsing the keywords case insensitively. Parsers for operators
   * and keywords can be obtained through { #token}; parsers for identifiers through
   * { #identifier}.
   * 
   * <p>In detail, keywords and operators are lexed as { Tokens.Fragment} with
   * { Tag#RESERVED} tag. Words that are not among {@code keywords} are lexed as
   * {@code Fragment} with { Tag#IDENTIFIER} tag.
   *
   * <p>A word is defined as an alphanumeric  string that starts with {@code [_a - zA - Z]},
   * with 0 or more {@code [0 - 9_a - zA - Z]} following.
   * 
   * @param ops the operator names.
   * @param keywords the keyword names.
   * @return the Terminals instance.
   * @deprecated Use {@code operators(ops)
   *                 .words(Scanners.IDENTIFIER)
   *                 .caseInsensitiveKeywords(keywords)
   *                 .build()} instead.
   */
  @Deprecated
  public static Terminals caseInsensitive(String[] ops, String[] keywords) {
    return operators(ops).words(Scanners.IDENTIFIER).caseInsensitiveKeywords(asList(keywords)).build();
  }
  
  /**
   * Returns a { Terminals} object for lexing and parsing the operators with names specified in
   * {@code ops}, and for lexing and parsing the keywords case sensitively. Parsers for operators
   * and keywords can be obtained through { #token}; parsers for identifiers through
   * { #identifier}.
   * 
   * <p>In detail, keywords and operators are lexed as { Tokens.Fragment} with
   * { Tag#RESERVED} tag. Words that are not among {@code keywords} are lexed as
   * {@code Fragment} with { Tag#IDENTIFIER} tag.
   *
   * <p>A word is defined as an alphanumeric string that starts with {@code [_a - zA - Z]},
   * with 0 or more {@code [0 - 9_a - zA - Z]} following.
   * 
   * @param ops the operator names.
   * @param keywords the keyword names.
   * @return the Terminals instance.
   * @deprecated Use {@code operators(ops)
   *                 .words(Scanners.IDENTIFIER)
   *                 .keywords(keywords)
   *                 .build()} instead.
   */
  @Deprecated
  public static Terminals caseSensitive(String[] ops, String[] keywords) {
    return operators(ops).words(Scanners.IDENTIFIER).keywords(asList(keywords)).build();
  }
  
  /**
   * Returns a { Terminals} object for lexing and parsing the operators with names specified in
   * {@code ops}, and for lexing and parsing the keywords case insensitively. Parsers for operators
   * and keywords can be obtained through { #token}; parsers for identifiers through
   * { #identifier}.
   * 
   * <p>In detail, keywords and operators are lexed as { Tokens.Fragment} with
   * { Tag#RESERVED} tag. Words that are not among {@code keywords} are lexed as
   * {@code Fragment} with { Tag#IDENTIFIER} tag.
   *  
   * @param wordScanner the scanner that returns a word in the language.
   * @param ops the operator names.
   * @param keywords the keyword names.
   * @return the Terminals instance.
   * @deprecated Use {@code operators(ops)
   *                 .words(wordScanner)
   *                 .caseInsensitiveKeywords(keywords)
   *                 .build()} instead.
   */
  @Deprecated
  public static Terminals caseInsensitive(
      Parser<String> wordScanner, String[] ops, String[] keywords) {
    return operators(ops)
        .words(wordScanner)
        .caseInsensitiveKeywords(keywords)
        .build();
  }
  
  /**
   * Returns a { Terminals} object for lexing and parsing the operators with names specified in
   * {@code ops}, and for lexing and parsing the keywords case sensitively. Parsers for operators
   * and keywords can be obtained through { #token}; parsers for identifiers through
   * { #identifier}.
   * 
   * <p>In detail, keywords and operators are lexed as { Tokens.Fragment} with
   * { Tag#RESERVED} tag. Words that are not among {@code keywords} are lexed as
   * {@code Fragment} with { Tag#IDENTIFIER} tag.
   *  
   * @param wordScanner the scanner that returns a word in the language.
   * @param ops the operator names.
   * @param keywords the keyword names.
   * @return the Terminals instance.
   * @deprecated Use {@code operators(ops)
   *                 .words(wordScanner)
   *                 .keywords(keywords)
   *                 .build()} instead.
   */
  @Deprecated
  public static Terminals caseSensitive(
      Parser<String> wordScanner, String[] ops, String[] keywords) {
    return operators(ops)
        .words(wordScanner)
        .keywords(keywords)
        .build();
  }
  
  /**
   * Returns a { Terminals} object for lexing and parsing the operators with names specified in
   * {@code ops}, and for lexing and parsing the keywords case insensitively. Parsers for operators
   * and keywords can be obtained through { #token}; parsers for identifiers through
   * { #identifier}.
   * 
   * <p>In detail, keywords and operators are lexed as { Tokens.Fragment} with
   * { Tag#RESERVED} tag. Words that are not among {@code keywords} are lexed as
   * {@code Fragment} with { Tag#IDENTIFIER} tag.
   *  
   * @param wordScanner the scanner that returns a word in the language.
   * @param ops the operator names.
   * @param keywords the keyword names.
   * @param wordMap maps the text to a token value for non-keywords recognized by
   * {@code wordScanner}.
   * @return the Terminals instance.
   * @deprecated Use {@code operators(ops)
   *                 .words(wordScanner)
   *                 .tokenizeWordsWith(wordMap)
   *                 .caseInsensitiveKeywords(keywords)
   *                 .build()} instead.
   */
  @Deprecated
  public static Terminals caseInsensitive(
      Parser<String> wordScanner, String[] ops, String[] keywords, Function<String, ?> wordMap) {
    return operators(ops)
        .words(wordScanner)
        .caseInsensitiveKeywords(keywords)
        .tokenizeWordsWith(wordMap)
        .build();
  }
  
  /**
   * Returns a { Terminals} object for lexing and parsing the operators with names specified in
   * {@code ops}, and for lexing and parsing the keywords case sensitively. Parsers for operators
   * and keywords can be obtained through { #token}; parsers for identifiers through
   * { #identifier}.
   * 
   * <p>In detail, keywords and operators are lexed as { Tokens.Fragment} with
   * { Tag#RESERVED} tag. Words that are not among {@code keywords} are lexed as
   * {@code Fragment} with { Tag#IDENTIFIER} tag.
   *  
   * @param wordScanner the scanner that returns a word in the language.
   * @param ops the operator names.
   * @param keywords the keyword names.
   * @param wordMap maps the text to a token value for non-keywords recognized by
   * {@code wordScanner}.
   * @return the Terminals instance.
   * @deprecated Use {@code operators(ops)
   *                 .words(wordScanner)
   *                 .tokenizeWordsWith(wordMap)
   *                 .keywords(keywords)
   *                 .build()} instead.
   */
  @Deprecated
  public static Terminals caseSensitive(
      Parser<String> wordScanner, String[] ops, String[] keywords, Function<String, ?> wordMap) {
    return operators(ops)
        .words(wordScanner)
        .keywords(keywords)
        .tokenizeWordsWith(wordMap)
        .build();
  }
  
  /**
   * Returns a { Terminals} object for lexing the operators with names specified in
   * {@code ops}. Operators are lexed as { Tokens.Fragment} with { Tag#RESERVED} tag.
   * For example, to get the parser for operator "?", simply call {@code token("?")}.
   *
   * <p>If words and keywords need to be parsed, they can be configured via { #words}.
   * 
   * @param ops the operator names.
   * @return the Terminals instance.
   */
  public static Terminals operators(String... ops) {
    return operators(asList(ops));
  }
  
  /**
   * Returns a { Terminals} object for lexing the operators with names specified in
   * {@code ops}. Operators are lexed as { Tokens.Fragment} with { Tag#RESERVED} tag.
   * For example, to get the parser for operator "?", simply call {@code token("?")}.
   *
   * <p>If words and keywords need to be parsed, they can be configured via { #words}.
   * 
   * @param ops the operator names.
   * @return the Terminals instance.
   * @since 2.2
   */
  public static Terminals operators(Collection<String> ops) {
    return new Terminals(Operators.lexicon(ops));
  }

  /**
   * Starts to build a new {@code Terminals} instance that recognizes words not already recognized
   * by {@code this} {@code Terminals} instance (typically operators).
   *
   * <p>By default identifiers are recognized through { #identifier} during token-level
   * parsing phase. Use { Builder#tokenizeWordsWith} to tokenize differently, and choose an
   * alternative token-level parser accordingly.
   *
   * @param wordScanner defines words recognized by the new instance
   * @since 2.2
   */
  public Builder words(Parser<String> wordScanner) {
    return new Builder(wordScanner);
  }

  /**
   * Builds { Terminals} instance by defining the words and keywords recognized.
   * The following example implements a calculator with logical operators: <pre>   {@code
   *   Terminals terms = Terminals
   *       .operators("<", ">", "=", ">=", "<=")
   *       .words(Scanners.IDENTIFIER)
   *       .caseInsensitiveKeywords("and", "or")
   *       .build();
   *   Parser<String> var = Terminals.identifier();
   *   Parser<Integer> integer = Terminals.IntegerLiteral.PARSER.map(...);
   *   Parser<?> and = terms.token("and");
   *   Parser<?> lessThan = terms.token("<");
   *   ...
   *   Parser<?> parser = grammar.from(
   *       terms.tokenizer().or(IntegerLiteral.TOKENIZER), Scanners.WHITSPACES.optional());
   * }</pre>
   *
   * @since 2.2
   */
  public final class Builder {
    private final Parser<String> wordScanner;

    private Collection<String> keywords = new ArrayList<String>();
    private StringCase stringCase = StringCase.CASE_SENSITIVE;
    private Function<String, ?> wordTokenMap = TokenizerMaps.IDENTIFIER_FRAGMENT;
    
    Builder(Parser<String> wordScanner) {
      this.wordScanner = Checks.checkNotNull(wordScanner);
    }

    /**
     * Defines keywords. Keywords are special words with their own grammar rules.
     * To get the parser for a keyword, call {@code token(keyword)}.
     *
     * <p>Note that if you call { #keywords} or { #caseInsensitiveKeywords} multiple
     * times on the same { Builder} instance, the last call overwrites previous calls.
     */
    public Builder keywords(@SuppressWarnings("hiding") String... keywords) {
      return keywords(asList(keywords));
    }

    /**
     * Defines keywords. Keywords are special words with their own grammar rules.
     * To get the parser for a keyword, call {@code token(keyword)}.
     *
     * <p>Note that if you call { #keywords} or { #caseInsensitiveKeywords} multiple
     * times on the same { Builder} instance, the last call overwrites previous calls.
     */
    public Builder keywords(@SuppressWarnings("hiding") Collection<String> keywords) {
      this.keywords = keywords;
      this.stringCase = StringCase.CASE_SENSITIVE;
      return this;
    }

    /**
     * Defines case insensitive keywords. Keywords are special words with their own grammar
     * rules. To get the parser for a keyword, call {@code token(keyword)}.
     *
     * <p>Note that if you call { #keywords} or { #caseInsensitiveKeywords} multiple
     * times on the same { Builder} instance, the last call overwrites previous calls.
     */
    public Builder caseInsensitiveKeywords(@SuppressWarnings("hiding") String... keywords) {
      return caseInsensitiveKeywords(asList(keywords));
    }

    /**
     * Defines case insensitive keywords. Keywords are special words with their own grammar
     * rules. To get the parser for a keyword, call {@code token(keyword)}.
     *
     * <p>Note that if you call { #keywords} or { #caseInsensitiveKeywords} multiple
     * times on the same { Builder} instance, the last call overwrites previous calls.
     */
    public Builder caseInsensitiveKeywords(@SuppressWarnings("hiding") Collection<String> keywords) {
      this.keywords = keywords;
      this.stringCase = StringCase.CASE_INSENSITIVE;
      return this;
    }

    /** Configures alternative tokenization strategy for words (except keywords). */
    public Builder tokenizeWordsWith(Function<String, ?> wordMap) {
      this.wordTokenMap = Checks.checkNotNull(wordMap);
      return this;
    }

    /** Builds a new { Terminals} instance that recognizes words defined in this builder. */
    public Terminals build() {
      return new Terminals(
          union(Keywords.lexicon(wordScanner, keywords, stringCase, wordTokenMap)));
    }
  }

  /**
   * Returns a { Parser} that recognizes identifiers (a.k.a words, variable names etc).
   * Equivalent to { Identifier#PARSER}.
   *
   * @since 2.2
   */
  public static Parser<String> identifier() {
    return Identifier.PARSER;
  }
  
  /**
   * Returns a { Parser} that recognizes { Tokens.Fragment} token values
   * tagged with one of {@code tags}. 
   */
  public static Parser<String> fragment(final Object... tags) {
    return Parsers.token(fromFragment(tags));
  }
  
  /**
   * Returns a { TokenMap} object that only recognizes { Tokens.Fragment} token values
   * tagged with one of {@code tags}.
   */
  static TokenMap<String> fromFragment(final Object... tags) {
    return new TokenMap<String>() {
      @Override public String map(final Token token) {
        final Object val = token.value();
        if (val instanceof Fragment) {
          Fragment c = (Fragment) val;
          if (!Objects.in(c.tag(), tags)) return null;
          return c.text();
        }
        else return null;
      }
      @Override public String toString() {
        if (tags.length == 0) return "";
        if (tags.length == 1) return String.valueOf(tags[0]);
        return "[" + Strings.join(", ", tags) + "]";
      }
    };
  }
  
  @Private static void checkDup(Iterable<String> a, Iterable<String> b) {
    for (String s1 : a) {
      for (String s2 : b) {
        Checks.checkArgument(!s1.equals(s2), "%s duplicated", s1);
      }
    }
  }
}
