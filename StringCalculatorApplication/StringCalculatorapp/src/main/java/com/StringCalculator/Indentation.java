

package com.StringCalculator;

import java.util.List;
import java.util.Stack;

import com.StringCalculator.internal.annotations.Private;
import com.StringCalculator.internal.util.Lists;
import com.StringCalculator.internal.util.Objects;
import com.StringCalculator.pattern.CharPredicate;
import com.StringCalculator.pattern.Pattern;
import com.StringCalculator.pattern.Patterns;

/**
 * Processes indentation
 * @author Rohit Amgaonkar
 */
public final class Indentation {
  
 
  static final CharPredicate INLINE_WHITESPACE = new CharPredicate() {
    @Override public boolean isChar(char c) {
      return c != '\n' && Character.isWhitespace(c);
    }
    @Override public String toString() {
      return "whitespace";
    }
  };
  
  
  static final Pattern LINE_CONTINUATION = Patterns.sequence(
      Patterns.isChar('\\'), Patterns.many(INLINE_WHITESPACE), Patterns.isChar('\n'));
  

  static final Pattern INLINE_WHITESPACES = Patterns.many1(INLINE_WHITESPACE);
  
  
  public static final Parser<Void> WHITESPACES =
      INLINE_WHITESPACES.or(LINE_CONTINUATION).many1().toScanner("whitespaces");
  
  @Private static enum Punctuation {
    INDENT, OUTDENT, LF
  }
  
  private final Object indent;
  private final Object outdent;
  
  
  public Indentation(Object indent, Object outdent) {
    this.indent = indent;
    this.outdent = outdent;
  }
  
  public Indentation() {
    this(Punctuation.INDENT, Punctuation.OUTDENT);
  }
  
  public Parser<Token> indent() {
    return token(indent);
  }
  
  /** A { Parser} that recognizes the generated {@code outdent} token. */
  public Parser<Token> outdent() {
    return token(outdent);
  }
  
  /**
   * A { Parser} that greedily runs {@code tokenizer}, and translates line feed characters
   * ({@code '\n'}) to {@code indent} and {@code outdent} tokens.
   * Return values are wrapped in { Token} objects and collected in a { List}.
   * Patterns recognized by {@code delim} are ignored. 
   */
  public Parser<List<Token>> lexer(Parser<?> tokenizer, Parser<?> delim) {
    Parser<?> lf = Scanners.isChar('\n').retn(Punctuation.LF);
    return Parsers.or(tokenizer, lf).lexer(delim)
        .map(tokens -> analyzeIndentations(tokens, Punctuation.LF));
  }
  
  private static Parser<Token> token(Object value) {
    return Parsers.token(InternalFunctors.tokenWithSameValue(value));
  }

  /**
   * Analyzes indentation by looking at the first token after each {@code lf} and inserting
   * {@code indent} and {@code outdent} tokens properly.
   */
  List<Token> analyzeIndentations(List<Token> tokens, Object lf) {
    if (tokens.isEmpty()) {
      return tokens;
    }
    int size = tokens.size();
    List<Token> result = Lists.arrayList(size + size / 16);
    Stack<Integer> indentations = new Stack<Integer>();
    boolean freshLine = true;
    int lfIndex = 0;
    for (Token token : tokens) {
      if (freshLine) {
        int indentation = token.index() - lfIndex;
        if (Objects.equals(token.value(), lf)) {
          // if first token on a line is lf, indentation is ignored.
          indentation = 0;
        }
        newLine(token, indentations, indentation, result);
      }
      if (Objects.equals(token.value(), lf)) {
        freshLine = true;
        lfIndex = token.index() + token.length();
      }
      else {
        freshLine = false;
        result.add(token);
      }
    }
    Token lastToken = tokens.get(tokens.size() - 1);
    int endIndex = lastToken.index() + lastToken.length();
    Token outdentToken = pseudoToken(endIndex, outdent);
    for (int i = 0; i < indentations.size() - 1; i++) {
      // add outdent for every remaining indentation except the first one
      result.add(outdentToken);
    }
    return result;
  }
  
  private void newLine(
      Token token, Stack<Integer> indentations, int indentation, List<Token> result) {
    for (;;) {
      if (indentations.isEmpty()) {
        indentations.add(indentation);
        return;
      }
      int previousIndentation = indentations.peek();
      if (previousIndentation < indentation) {
        // indent
        indentations.push(indentation);
        result.add(pseudoToken(token.index(), indent));
        return;
      }
      else if (previousIndentation > indentation) {
        // outdent
        indentations.pop();
        if (indentations.isEmpty()) {
          return;
        }
        result.add(pseudoToken(token.index(), outdent));
        continue;
      }
      return;
    }
  }
  
  private static Token pseudoToken(int index, Object value) {
    return new Token(index, 0, value);
  }
}
