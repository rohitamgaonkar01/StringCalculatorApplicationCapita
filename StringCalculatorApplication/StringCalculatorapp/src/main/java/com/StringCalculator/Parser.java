
package com.StringCalculator;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.StringCalculator.error.ParserException;
import com.StringCalculator.internal.annotations.Private;
import com.StringCalculator.internal.util.Checks;

/**
 * Defines grammar and encapsulates parsing logic. A { Parser} takes as input a
 * { CharSequence} source and parses it when the { #parse(CharSequence)} method is called.
 * A value of type {@code T} will be returned if parsing succeeds, or a { ParserException}
 * is thrown to indicate parsing error. For example: <pre>   {@code
 *   Parser<String> scanner = Scanners.IDENTIFIER;
 *   assertEquals("foo", scanner.parse("foo"));
 * }</pre>
 *
 * <p> {@code Parser}s run either on character level to scan the source, or on token level to parse
 * a list of { Token} objects returned from another parser. This other parser that returns the
 * list of tokens for token level parsing is hooked up via the { #from(Parser, Parser)}
 * or { #from(Parser)} method.
 *
 * <p>The following are important naming conventions used throughout the library:
 *
 * <ul>
 * <li>A character level parser object that recognizes a single lexical word is called a scanner.
 * <li>A scanner that translates the recognized lexical word into a token is called a tokenizer.
 * <li>A character level parser object that does lexical analysis and returns a list of
 *     { Token} is called a lexer.
 * <li>All {@code index} parameters are 0-based indexes in the original source.
 * </ul>
 *
 * To debug a complex parser that fails in un-obvious way, pass { Mode#DEBUG} mode to
 * { #parse(CharSequence, Mode)} and inspect the result in
 * { ParserException#getParseTree()}. All { #label labeled} parsers will generate a node
 * in the exception's parse tree, with matched indices in the source.
 *
 * @author Rohit Amgaonkar
 */
public abstract class Parser<T> {

  /**
   * An atomic mutable reference to { Parser} used in recursive grammars.
   *
   * <p>For example, the following is a recursive grammar for a simple calculator: <pre>   {@code
   *   Terminals terms = Terminals.operators("(", ")", "+", "-");
   *   Parser.Reference<Integer> ref = Parser.newReference();
   *   Parser<Integer> literal = Terminals.IntegerLiteral.PARSER.map(new Function<String, Integer>() {
   *      ...
   *      return Integer.parseInt(s);
   *   });
   *   Parser.Reference<Integer> parenthesized =  // recursion in rule E = (E)
   *       Parsers.between(terms.token("("), ref.lazy(), terms.token(")"));
   *   ref.set(new OperatorTable()
   *       .infixl(terms.token("+").retn(plus), 10)
   *       .infixl(terms.token("-").retn(minus), 10)
   *       .build(literal.or(parenthesized)));
   *   return ref.get();
   * }</pre>
   * Note that a left recursive grammar will result in {@code StackOverflowError}.
   * Use appropriate parser built-in parser combinators to avoid left-recursion.
   * For instance, many left recursive grammar rules can be thought as logically equivalent to
   * postfix operator rules. In such case, either { OperatorTable} or { Parser#postfix}
   * can be used to work around left recursion.
   * The following is a left recursive parser for array types in the form of "T[]" or "T[][]":
   * <pre>   {@code
   *   Terminals terms = Terminals.operators("[", "]");
   *   Parser.Reference<Type> ref = Parser.newReference();
   *   ref.set(Parsers.or(leafTypeParser,
   *       Parsers.sequence(ref.lazy(), terms.phrase("[", "]"), new Unary<Type>() {...})));
   *   return ref.get();
   * }</pre>
   * And it will fail. A correct implementation is:  <pre>   {@code
   *   Terminals terms = Terminals.operators("[", "]");
   *   return leafTypeParer.postfix(terms.phrase("[", "]").retn(new Unary<Type>() {...}));
   * }</pre>
   * A not-so-obvious example, is to parse the {@code expr ? a : b} ternary operator. It too is a
   * left recursive grammar. And un-intuitively it can also be thought as a postfix operator.
   * Basically, we can parse "? a : b" as a whole into a unary operator that accepts the condition
   * expression as input and outputs the full ternary expression: <pre>   {@code
   *   Parser<Expr> ternary(Parser<Expr> expr) {
   *     return expr.postfix(
   *       Parsers.sequence(
   *           terms.token("?"), expr, terms.token(":"), expr,
   *           (unused, then, unused, orelse) -> cond ->
   *               new TernaryExpr(cond, then, orelse)));
   *   }
   * }</pre>
   */
  @SuppressWarnings("serial")
  public static final class Reference<T> extends AtomicReference<Parser<T>> {
    private final Parser<T> lazy = new Parser<T>() {
      @Override boolean apply(ParseContext ctxt) {
        return deref().apply(ctxt);
      }
      private Parser<T> deref() {
        Parser<T> p = get();
        Checks.checkNotNullState(p,
            "Uninitialized lazy parser reference. Did you forget to call set() on the reference?");
        return p;
      }
      @Override public String toString() {
        return "lazy";
      }
    };

    /**
     * A { Parser} that delegates to the parser object referenced by {@code this} during parsing time.
     */
    public Parser<T> lazy() {
      return lazy;
    }
  }

  Parser() {}

  /**
   * Creates a new instance of { Reference}.
   * Used when your grammar is recursive (many grammars are).
   */
  public static <T> Reference<T> newReference() {
    return new Reference<T>();
  }

  /**
   * A { Parser} that executes {@code this}, and returns {@code value} if succeeds.
   */
  public final <R> Parser<R> retn(R value) {
    return next(Parsers.constant(value));
  }

  /**
   * A { Parser} that sequentially executes {@code this} and then {@code parser}. The return value of {@code
   * parser} is preserved.
   */
  public final <R> Parser<R> next(Parser<R> parser) {
    return Parsers.sequence(this, parser);
  }

  /**
   * A { Parser} that executes {@code this}, maps the result using {@code map} to another {@code Parser} object
   * to be executed as the next step.
   */
  public final <To> Parser<To> next(
      final Function<? super T, ? extends Parser<? extends To>> map) {
    return new Parser<To>() {
      @Override boolean apply(ParseContext ctxt) {
        return Parser.this.apply(ctxt) && runNext(ctxt);
      }
      @Override public String toString() {
        return map.toString();
      }
      private boolean runNext(ParseContext state) {
        T from = Parser.this.getReturn(state);
        return map.apply(from).apply(state);
      }
    };
  }

  /**
   * A { Parser} that matches this parser zero or many times
   * until the given parser succeeds. The input that matches the given parser
   * will not be consumed. The input that matches this parser will
   * be collected in a list that will be returned by this function.
   *
   * @since 2.2
   */
  public final Parser<List<T>> until(Parser<?> parser) {
    return parser.not().next(this).many().followedBy(parser.peek());
  }

  /**
   * A { Parser} that sequentially executes {@code this} and then {@code parser}, whose return value is ignored.
   */
  public final Parser<T> followedBy(Parser<?> parser) {
    return Parsers.sequence(this, parser, InternalFunctors.<T, Object>firstOfTwo());
  }

  /**
   * A { Parser} that succeeds if {@code this} succeeds and the pattern recognized by {@code parser} isn't
   * following.
   */
  public final Parser<T> notFollowedBy(Parser<?> parser) {
    return followedBy(parser.not());
  }

  /**
   * {@code p.many()} is equivalent to {@code p*} in EBNF. The return values are collected and returned in a {
   * List}.
   */
  public final Parser<List<T>> many() {
    return atLeast(0);
  }

  /**
   * {@code p.skipMany()} is equivalent to {@code p*} in EBNF. The return values are discarded.
   */
  public final Parser<Void> skipMany() {
    return skipAtLeast(0);
  }

  /**
   * {@code p.many1()} is equivalent to {@code p+} in EBNF. The return values are collected and returned in a {
   * List}.
   */
  public final Parser<List<T>> many1() {
    return atLeast(1);
  }

  /**
   * {@code p.skipMany1()} is equivalent to {@code p+} in EBNF. The return values are discarded.
   */
  public final Parser<Void> skipMany1() {
    return skipAtLeast(1);
  }

  /**
   * A { Parser} that runs {@code this} parser greedily for at least {@code min} times. The return values are
   * collected and returned in a { List}.
   */
  public final Parser<List<T>> atLeast(int min) {
    return new RepeatAtLeastParser<T>(this, Checks.checkMin(min));
  }

  /**
   * A { Parser} that runs {@code this} parser greedily for at least {@code min} times and ignores the return
   * values.
   */
  public final Parser<Void> skipAtLeast(int min) {
    return new SkipAtLeastParser(this, Checks.checkMin(min));
  }

  /**
   * A { Parser} that sequentially runs {@code this} for {@code n} times and ignores the return values.
   */
  public final Parser<Void> skipTimes(int n) {
    return skipTimes(n, n);
  }

  /**
   * A { Parser} that runs {@code this} for {@code n} times and collects the return values in a { List}.
   */
  public final Parser<List<T>> times(int n) {
    return times(n, n);
  }

  /**
   * A { Parser} that runs {@code this} parser for at least {@code min} times and up to {@code max} times. The
   * return values are collected and returned in { List}.
   */
  public final Parser<List<T>> times(int min, int max) {
    Checks.checkMinMax(min, max);
    return new RepeatTimesParser<T>(this, min, max);
  }

  /**
   * A { Parser} that runs {@code this} parser for at least {@code min} times and up to {@code max} times, with
   * all the return values ignored.
   */
  public final Parser<Void> skipTimes(int min, int max) {
    Checks.checkMinMax(min, max);
    return new SkipTimesParser(this, min, max);
  }

  /**
   * A { Parser} that runs {@code this} parser and transforms the return value using {@code map}.
   */
  public final <R> Parser<R> map(final Function<? super T, ? extends R> map) {
    return new Parser<R>() {
      @Override boolean apply(final ParseContext ctxt) {
        final boolean r = Parser.this.apply(ctxt);
        if (r) {
          ctxt.result = map.apply(Parser.this.getReturn(ctxt));
        }
        return r;
      }
      @Override public String toString() {
        return map.toString();
      }
    };
  }

  /**
   * {@code p1.or(p2)} is equivalent to {@code p1 | p2} in EBNF.
   *
   * @param alternative the alternative parser to run if this fails.
   */
  public final Parser<T> or(Parser<? extends T> alternative) {
    return Parsers.or(this, alternative);
  }

  /**
   * {@code p.optional()} is equivalent to {@code p?} in EBNF. {@code null} is the result when
   * {@code this} fails with no partial match.
   *
   * @deprecated since 3.0. Use { #optional(null)} or { #optional_} instead.
   */
  @Deprecated
  public final Parser<T> optional() {
    return Parsers.or(this, Parsers.<T>always());
  }

  /**
   * {@code p.asOptional()} is equivalent to {@code p?} in EBNF. {@code Optional.empty()}
   * is the result when {@code this} fails with no partial match. Note that { Optional}
   * prohibits nulls so make sure {@code this} does not result in {@code null}.
   *
   * @since 3.0
   */
  public final Parser<Optional<T>> asOptional() {
    return map(Optional::of).optional(Optional.empty());
  }

  /**
   * A { Parser} that returns {@code defaultValue} if {@code this} fails with no partial match.
   */
  public final Parser<T> optional(T defaultValue) {
    return Parsers.or(this, Parsers.constant(defaultValue));
  }

  /**
   * A { Parser} that fails if {@code this} succeeds. Any input consumption is undone.
   */
  public final Parser<?> not() {
    return not(toString());
  }

  /**
   * A { Parser} that fails if {@code this} succeeds. Any input consumption is undone.
   *
   * @param unexpected the name of what we don't expect.
   */
  public final Parser<?> not(String unexpected) {
    return peek().ifelse(Parsers.unexpected(unexpected), Parsers.always());
  }

  /**
   * A { Parser} that runs {@code this} and undoes any input consumption if succeeds.
   */
  public final Parser<T> peek() {
    return new Parser<T>() {
      @Override public Parser<T> label(String name) {
        return Parser.this.label(name).peek();
      }
      @Override boolean apply(ParseContext ctxt) {
        int step = ctxt.step;
        int at = ctxt.at;
        boolean ok = Parser.this.apply(ctxt);
        if (ok) ctxt.setAt(step, at);
        return ok;
      }
      @Override public String toString() {
        return "peek";
      }
    };
  }

  /**
   * A { Parser} that undoes any partial match if {@code this} fails. In other words, the
   * parser either fully matches, or matches none.
   */
  public final Parser<T> atomic() {
    return new Parser<T>() {
      @Override public Parser<T> label(String name) {
        return Parser.this.label(name).atomic();
      }
      @Override boolean apply(ParseContext ctxt) {
        int at = ctxt.at;
        int step = ctxt.step;
        boolean r = Parser.this.apply(ctxt);
        if (r) ctxt.step = step + 1;
        else ctxt.setAt(step, at);
        return r;
      }
      @Override public String toString() {
        return Parser.this.toString();
      }
    };
  }

  /**
   * A { Parser} that returns {@code true} if {@code this} succeeds, {@code false} otherwise.
   */
  public final Parser<Boolean> succeeds() {
    return ifelse(Parsers.TRUE, Parsers.FALSE);
  }

  /**
   * A { Parser} that returns {@code true} if {@code this} fails, {@code false} otherwise.
   */
  public final Parser<Boolean> fails() {
    return ifelse(Parsers.FALSE, Parsers.TRUE);
  }

  /**
   * A { Parser} that runs {@code consequence} if {@code this} succeeds, or {@code alternative} otherwise.
   */
  public final <R> Parser<R> ifelse(Parser<? extends R> consequence, Parser<? extends R> alternative) {
    return ifelse(__ -> consequence, alternative);
  }

  /**
   * A { Parser} that runs {@code consequence} if {@code this} succeeds, or {@code alternative} otherwise.
   */
  public final <R> Parser<R> ifelse(
      final Function<? super T, ? extends Parser<? extends R>> consequence,
      final Parser<? extends R> alternative) {
    return new Parser<R>() {
      @Override boolean apply(ParseContext ctxt) {
        final Object ret = ctxt.result;
        final int step = ctxt.step;
        final int at = ctxt.at;
        if (ctxt.withErrorSuppressed(Parser.this)) {
          Parser<? extends R> parser = consequence.apply(Parser.this.getReturn(ctxt));
          return parser.apply(ctxt);
        }
        ctxt.set(step, at, ret);
        return alternative.apply(ctxt);
      }
      @Override public String toString() {
        return "ifelse";
      }
    };
  }

  /**
   * A { Parser} that reports reports an error about {@code name} expected, if {@code this} fails with no partial
   * match.
   */
  public Parser<T> label(final String name) {
    return new Parser<T>() {
      @Override public Parser<T> label(String overrideName) {
        return Parser.this.label(overrideName);
      }
      @Override boolean apply(ParseContext ctxt) {
        return ctxt.applyNewNode(Parser.this, name);
      }
      @Override public String toString() {
        return name;
      }
    };
  }

  /**
   * Casts {@code this} to a { Parser} of type {@code R}. Use it only if you know the parser actually returns
   * value of type {@code R}.
   */
  @SuppressWarnings("unchecked")
  public final <R> Parser<R> cast() {
    return (Parser<R>) this;
  }

  /**
   * A { Parser} that runs {@code this} between {@code before} and {@code after}. The return value of {@code
   * this} is preserved.
   *
   * <p>Equivalent to { Parsers#between(Parser, Parser, Parser)}, which preserves the natural order of the
   * parsers in the argument list, but is a bit more verbose.
   */
  public final Parser<T> between(Parser<?> before, Parser<?> after) {
    return before.next(followedBy(after));
  }
  
  /**
   * A { Parser} that first runs {@code before} from the input start, 
   * then runs {@code after} from the input's end, and only
   * then runs {@code this} on what's left from the input.
   * In effect, {@code this} behaves reluctantly, giving
   * {@code after} a chance to grab input that would have been consumed by {@code this}
   * otherwise.
   * @deprecated This method probably only works in the simplest cases. And it's a character-level
   * parser only. Use it at your own risk. It may be deleted later when we find a better way.
   */
  @Deprecated
  public final Parser<T> reluctantBetween(Parser<?> before, Parser<?> after) {
    return new ReluctantBetweenParser<T>(before, this, after);
  }

  /**
   * A { Parser} that runs {@code this} 1 or more times separated by {@code delim}.
   *
   * <p>The return values are collected in a { List}.
   */
  public final Parser<List<T>> sepBy1(Parser<?> delim) {
    final Parser<T> afterFirst = delim.asDelimiter().next(this);
    return next((Function<T, Parser<List<T>>>) firstValue ->
        new RepeatAtLeastParser<T>(
            afterFirst, 0, ListFactory.arrayListFactoryWithFirstElement(firstValue)));
  }

  /**
   * A { Parser} that runs {@code this} 0 or more times separated by {@code delim}.
   *
   * <p>The return values are collected in a { List}.
   */
  public final Parser<List<T>> sepBy(Parser<?> delim) {
    return Parsers.or(sepBy1(delim), EmptyListParser.<T>instance());
  }

  /**
   * A { Parser} that runs {@code this} for 0 or more times delimited and terminated by
   * {@code delim}.
   *
   * <p>The return values are collected in a { List}.
   */
  public final Parser<List<T>> endBy(Parser<?> delim) {
    return followedBy(delim).many();
  }

  /**
   * A { Parser} that runs {@code this} for 1 or more times delimited and terminated by {@code delim}.
   *
   * <p>The return values are collected in a { List}.
   */
  public final Parser<List<T>> endBy1(Parser<?> delim) {
    return followedBy(delim).many1();
  }

  /**
   * A { Parser} that runs {@code this} for 1 ore more times separated and optionally terminated by {@code
   * delim}. For example: {@code "foo;foo;foo"} and {@code "foo;foo;"} both matches {@code foo.sepEndBy1(semicolon)}.
   *
   * <p>The return values are collected in a { List}.
   */
  public final Parser<List<T>> sepEndBy1(final Parser<?> delim) {
    return next(first ->
        new DelimitedParser<T>(this, delim, ListFactory.arrayListFactoryWithFirstElement(first)));
  }

  /**
   * A { Parser} that runs {@code this} for 0 ore more times separated and optionally terminated by {@code
   * delim}. For example: {@code "foo;foo;foo"} and {@code "foo;foo;"} both matches {@code foo.sepEndBy(semicolon)}.
   *
   * <p>The return values are collected in a { List}.
   */
  public final Parser<List<T>> sepEndBy(Parser<?> delim) {
    return Parsers.or(sepEndBy1(delim), EmptyListParser.<T>instance());
  }

  /**
   * A { Parser} that runs {@code op} for 0 or more times greedily, then runs {@code this}.
   * The { Function} objects returned from {@code op} are applied from right to left to the
   * return value of {@code p}.
   *
   * <p> {@code p.prefix(op)} is equivalent to {@code op* p} in EBNF.
   */
  public final Parser<T> prefix(Parser<? extends Function<? super T, ? extends T>> op) {
    return Parsers.sequence(op.many(), this, Parser::applyPrefixOperators);
  }

  /**
   * A { Parser} that runs {@code this} and then runs {@code op} for 0 or more times greedily.
   * The { Function} objects returned from {@code op} are applied from left to right to the return
   * value of p.
   *
   * <p>This is the preferred API to avoid {@code StackOverflowError} in left-recursive parsers.
   * For example, to parse array types in the form of "T[]" or "T[][]", the following
   * left recursive grammar will fail: <pre>   {@code
   *   Terminals terms = Terminals.operators("[", "]");
   *   Parser.Reference<Type> ref = Parser.newReference();
   *   ref.set(Parsers.or(leafTypeParser,
   *       Parsers.sequence(ref.lazy(), terms.phrase("[", "]"), new Unary<Type>() {...})));
   *   return ref.get();
   * }</pre>
   * A correct implementation is:  <pre>   {@code
   *   Terminals terms = Terminals.operators("[", "]");
   *   return leafTypeParer.postfix(terms.phrase("[", "]").retn(new Unary<Type>() {...}));
   * }</pre>
   * A not-so-obvious example, is to parse the {@code expr ? a : b} ternary operator. It too is a
   * left recursive grammar. And un-intuitively it can also be thought as a postfix operator.
   * Basically, we can parse "? a : b" as a whole into a unary operator that accepts the condition
   * expression as input and outputs the full ternary expression: <pre>   {@code
   *   Parser<Expr> ternary(Parser<Expr> expr) {
   *     return expr.postfix(
   *       Parsers.sequence(
   *           terms.token("?"), expr, terms.token(":"), expr,
   *           (unused, then, unused, orelse) -> cond ->
   *               new TernaryExpr(cond, then, orelse)));
   *   }
   * }</pre>
   * { OperatorTable} also handles left recursion transparently.
   *
   * <p> {@code p.postfix(op)} is equivalent to {@code p op*} in EBNF.
   */
  public final Parser<T> postfix(Parser<? extends Function<? super T, ? extends T>> op) {
    return Parsers.sequence(this, op.many(), Parser::applyPostfixOperators);
  }

  /**
   * A { Parser} that parses non-associative infix operator.
   * Runs {@code this} for the left operand, and then
   * runs {@code op} and {@code this} for the operator and the right operand optionally.
   * The { BiFunction} objects
   * returned from {@code op} are applied to the return values of the two operands, if any.
   *
   * <p> {@code p.infixn(op)} is equivalent to {@code p (op p)?} in EBNF.
   */
  public final Parser<T> infixn(Parser<? extends BiFunction<? super T, ? super T, ? extends T>> op) {
    return next(a -> {
      Parser<T> shift = Parsers.sequence(op, this, (m2, b) -> m2.apply(a, b));
      return shift.or(Parsers.constant(a));
    });
  }

  /**
   * A { Parser} for left-associative infix operator. Runs {@code this} for the left operand, and then runs
   * {@code operator} and {@code this} for the operator and the right operand for 0 or more times greedily.
   * The { BiFunction} objects returned from {@code operator} are applied from left to right to the
   * return values of {@code this}, if any. For example:
   * {@code a + b + c + d} is evaluated as {@code (((a + b)+c)+d)}.
   *
   * <p> {@code p.infixl(op)} is equivalent to {@code p (op p)*} in EBNF.
   */
  public final Parser<T> infixl(
      Parser<? extends BiFunction<? super T, ? super T, ? extends T>> operator) {
    BiFunction<BiFunction<? super T, ? super T, ? extends T>, T, Function<? super T, ? extends T>>
    rightToLeft = (op, r) -> l -> op.apply(l, r);
    return next(first ->
        Parsers.sequence(operator, this, rightToLeft)
            .many()
            .map(maps -> applyInfixOperators(first, maps)));
  }

  /**
   * A { Parser} for right-associative infix operator. Runs {@code this} for the left operand,
   * and then runs {@code op} and {@code this} for the operator and the right operand for
   * 0 or more times greedily.
   * The { BiFunction} objects returned from {@code op} are applied from right to left to the
   * return values of {@code this}, if any. For example: {@code a + b + c + d} is evaluated as
   * {@code a + (b + (c + d))}.
   *
   * <p> {@code p.infixr(op)} is equivalent to {@code p (op p)*} in EBNF.
   */
  public final Parser<T> infixr(Parser<? extends BiFunction<? super T, ? super T, ? extends T>> op) {
    Parser<Rhs<T>> rhs = Parsers.sequence(op, this, Rhs<T>::new);
    return Parsers.sequence(this, rhs.many(), Parser::applyInfixrOperators);
  }

  /**
   * A { Parser} that runs {@code this} and wraps the return value in a { Token}.
   *
   * <p>It is normally not necessary to call this method explicitly. { #lexer(Parser)} and { #from(Parser,
   * Parser)} both do the conversion automatically.
   */
  public final Parser<Token> token() {
    return new Parser<Token>() {
      @Override boolean apply(ParseContext ctxt) {
        int begin = ctxt.getIndex();
        if (!Parser.this.apply(ctxt)) {
          return false;
        }
        int len = ctxt.getIndex() - begin;
        Token token = new Token(begin, len, ctxt.result);
        ctxt.result = token;
        return true;
      }
      @Override public String toString() {
        return Parser.this.toString();
      }
    };
  }

  /**
   * A { Parser} that returns the matched string in the original source.
   */
  public final Parser<String> source() {
    return new Parser<String>() {
      @Override boolean apply(ParseContext ctxt) {
        int begin = ctxt.getIndex();
        if (!Parser.this.apply(ctxt)) {
          return false;
        }
        ctxt.result = ctxt.source.subSequence(begin, ctxt.getIndex()).toString();
        return true;
      }
      @Override public String toString() {
        return "source";
      }
    };
  }

  /**
   * A { Parser} that returns both parsed object and matched string.
   */
  public final Parser<WithSource<T>> withSource() {
    return new Parser<WithSource<T>>() {
      @Override boolean apply(ParseContext ctxt) {
        int begin = ctxt.getIndex();
        if (!Parser.this.apply(ctxt)) {
          return false;
        }
        String source = ctxt.source.subSequence(begin, ctxt.getIndex()).toString();
        @SuppressWarnings("unchecked")
        WithSource<T> withSource = new WithSource<T>((T) ctxt.result, source);
        ctxt.result = withSource;
        return true;
      }
      @Override public String toString() {
        return Parser.this.toString();
      }
    };
  }

  /**
   * A { Parser} that takes as input the { Token} collection returned by {@code lexer},
   * and runs {@code this} to parse the tokens. Most parsers should use the simpler
   * { #from(Parser, Parser)} instead.
   *
   * <p> {@code this} must be a token level parser.
   */
  public final Parser<T> from(Parser<? extends Collection<Token>> lexer) {
    return Parsers.nested(Parsers.tokens(lexer), followedBy(Parsers.EOF));
  }

  /**
   * A { Parser} that takes as input the tokens returned by {@code tokenizer} delimited by
   * {@code delim}, and runs {@code this} to parse the tokens. A common misunderstanding is that
   * {@code tokenizer} has to be a parser of { Token}. It doesn't need to be because
   * {@code Terminals} already takes care of wrapping your logical token objects into physical
   * {@code Token} with correct source location information tacked on for free. Your token object
   * can literally be anything, as long as your token level parser can recognize it later.
   *
   * <p>The following example uses {@code Terminals.tokenizer()}: <pre class="code">
   * Terminals terminals = ...;
   * return parser.from(terminals.tokenizer(), Scanners.WHITESPACES.optional()).parse(str);
   * </pre>
   * And tokens are optionally delimited by whitespaces.
   * <p>Optionally, you can skip comments using an alternative scanner than {@code WHITESPACES}:
   * <pre class="code">   {@code
   *   Terminals terminals = ...;
   *   Parser<?> delim = Parsers.or(
   *       Scanners.WHITESPACE,
   *       Scanners.JAVA_LINE_COMMENT,
   *       Scanners.JAVA_BLOCK_COMMENT).skipMany();
   *   return parser.from(terminals.tokenizer(), delim).parse(str);
   * }</pre>
   *
   * <p>In both examples, it's important to make sure the delimiter scanner can accept empty string
   * (either through { #optional} or { #skipMany}), unless adjacent operator
   * characters shouldn't be parsed as separate operators.
   * i.e. "((" as two left parenthesis operators.
   *
   * <p> {@code this} must be a token level parser.
   */
  public final Parser<T> from(Parser<?> tokenizer, Parser<Void> delim) {
    return from(tokenizer.lexer(delim));
  }

  /**
   * A { Parser} that greedily runs {@code this} repeatedly, and ignores the pattern recognized by {@code delim}
   * before and after each occurrence. The result tokens are wrapped in { Token} and are collected and returned
   * in a { List}.
   *
   * <p>It is normally not necessary to call this method explicitly. { #from(Parser, Parser)} is more convenient
   * for simple uses that just need to connect a token level parser with a lexer that produces the tokens. When more
   * flexible control over the token list is needed, for example, to parse indentation sensitive language, a
   * pre-processor of the token list may be needed.
   *
   * <p> {@code this} must be a tokenizer that returns a token value.
   */
  public Parser<List<Token>> lexer(Parser<?> delim) {
    return delim.optional(null).next(token().sepEndBy(delim));
  }

  /**
   * As a delimiter, the parser's error is considered lenient and will only be reported if no other
   * meaningful error is encountered. The delimiter's logical step is also considered 0, which means
   * it won't ever stop repetition combinators such as { #many}.
   */
  final Parser<T> asDelimiter() {
    return new Parser<T>() {
      @Override boolean apply(ParseContext ctxt) {
        return ctxt.applyAsDelimiter(Parser.this);
      }
      @Override public String toString() {
        return Parser.this.toString();
      }
    };
  }

  /**
   * Parses {@code source}.
   */
  public final T parse(CharSequence source) {
    return parse(source, Mode.PRODUCTION);
  }

  /**
   * Parses source read from {@code readable}.
   */
  public final T parse(Readable readable) throws IOException {
    return parse(read(readable));
  }

  /**
   * Parses {@code source} under the given {@code mode}. For example: <pre>
   *   try {
   *     parser.parse(text, Mode.DEBUG);
   *   } catch (ParserException e) {
   *     ParseTree parseTree = e.getParseTree();
   *     ...
   *   }
   * </pre>
   *
   * @since 2.3
   */
  public final T parse(CharSequence source, Mode mode) {
    return mode.run(this, source);
  }

  /**
   * Parses {@code source} and returns a { ParseTree} corresponding to the syntactical
   * structure of the input. Only { #label labeled} parser nodes are represented in the parse
   * tree.
   *
   * <p>If parsing failed, { ParserException#getParseTree()} can be inspected for the parse
   * tree at error location.
   *
   * @since 2.3
   */
  public final ParseTree parseTree(CharSequence source) {
    ScannerState state = new ScannerState(source);
    state.enableTrace("root");
    state.run(this.followedBy(Parsers.EOF));
    return state.buildParseTree();
  }

  /**
   * Defines the mode that a parser should be run in.
   *
   * @since 2.3
   */
  public enum Mode {
    /** Default mode. Used for production. */
    PRODUCTION {
      @Override <T> T run(Parser<T> parser, CharSequence source) {
        return new ScannerState(source).run(parser.followedBy(Parsers.EOF));
      }
    },

    /**
     * Debug mode. { ParserException#getParseTree} can be used to inspect partial parse result.
     */
    DEBUG {
      @Override <T> T run(Parser<T> parser, CharSequence source) {
        ScannerState state = new ScannerState(source);
        state.enableTrace("root");
        return state.run(parser.followedBy(Parsers.EOF));
      }
    }
    ;
    abstract <T> T run(Parser<T> parser, CharSequence source);
  }

  /**
   * Parses {@code source}.
   *
   * @param source     the source string
   * @param moduleName the name of the module, this name appears in error message
   * @return the result
   * @deprecated Please use { #parse(CharSequence)} instead.
   */
  @Deprecated
  public final T parse(CharSequence source, String moduleName) {
    return new ScannerState(moduleName, source, 0, new SourceLocator(source))
        .run(followedBy(Parsers.EOF));
  }

  /**
   * Parses source read from {@code readable}.
   *
   * @param readable   where the source is read from
   * @param moduleName the name of the module, this name appears in error message
   * @return the result
   * @deprecated Please use { #parse(Readable)} instead.
   */
  @Deprecated
  public final T parse(Readable readable, String moduleName) throws IOException {
    return parse(read(readable), moduleName);
  }
  
  abstract boolean apply(ParseContext ctxt);

  /**
   * Copies all content from {@code from} to {@code to}.
   */
  @Private
  static StringBuilder read(Readable from) throws IOException {
    StringBuilder builder = new StringBuilder();
    CharBuffer buf = CharBuffer.allocate(2048);
    for (; ; ) {
      int r = from.read(buf);
      if (r == -1) break;
      buf.flip();
      builder.append(buf, 0, r);
    }
    return builder;
  }

  @SuppressWarnings("unchecked")
  final T getReturn(ParseContext ctxt) {
    return (T) ctxt.result;
  }

  private static <T> T applyPrefixOperators(
      List<? extends Function<? super T, ? extends T>> ms, T a) {
    for (int i = ms.size() - 1; i >= 0; i--) {
      Function<? super T, ? extends T> m = ms.get(i);
      a = m.apply(a);
    }
    return a;
  }

  private static <T> T applyPostfixOperators(
      T a, Iterable<? extends Function<? super T, ? extends T>> ms) {
    for (Function<? super T, ? extends T> m : ms) {
      a = m.apply(a);
    }
    return a;
  }

  private static <T> T applyInfixOperators(
      T initialValue, List<? extends Function<? super T, ? extends T>> functions) {
    T result = initialValue;
    for (Function<? super T, ? extends T> function : functions) {
      result = function.apply(result);
    }
    return result;
  }

  // 1+ 1+ 1+ ..... 1
  private static final class Rhs<T> {
    final BiFunction<? super T, ? super T, ? extends T> op;
    final T rhs;

    Rhs(BiFunction<? super T, ? super T, ? extends T> op, T rhs) {
      this.op = op;
      this.rhs = rhs;
    }
    
    @Override public String toString() {
      return op + " " + rhs;
    }
  }

  private static <T> T applyInfixrOperators(T first, List<Rhs<T>> rhss) {
    if (rhss.isEmpty()) return first;
    int lastIndex = rhss.size() - 1;
    T o2 = rhss.get(lastIndex).rhs;
    for (int i = lastIndex; i > 0; i--) {
      T o1 = rhss.get(i - 1).rhs;
      o2 = rhss.get(i).op.apply(o1, o2);
    }
    return rhss.get(0).op.apply(first, o2);
  }
}
