
package com.StringCalculator;

/**
 * Represents { ParseContext} for token level parsing.
 * 
 * @author Rohit Amgaonkar
 */
final class ParserState extends ParseContext {
  
  private static final String USED_ON_TOKEN_INPUT = "Cannot scan characters on tokens."
      + "\nThis normally happens when you are using a character-level parser on token input."
      + " For example: Scanners.string(foo).from(tokenizer).parse(text) will result in this error"
      + " because scanner works on characters while it's used as a token-level parser.";

  private final Token[] input;
  
  // in case a terminating eof token is not explicitly created, the implicit one is used.
  private final int endIndex;

  @Override boolean isEof() {
    return at >= input.length;
  }
  
  @Override int toIndex(int pos) {
    if (pos >= input.length) return endIndex;
    return input[pos].index();
  }

  @Override Token getToken() {
    return input[at];
  }
  
  ParserState(String module, CharSequence source, Token[] input, int at,
      SourceLocator locator, int endIndex, Object result) {
    super(source, result, at, module, locator);
    this.input = input;
    this.endIndex = endIndex;
  }
  
  @Override char peekChar() {
    throw new IllegalStateException(USED_ON_TOKEN_INPUT);
  }
  
  @Override CharSequence characters() {
    throw new IllegalStateException(USED_ON_TOKEN_INPUT);
  }

  @Override String getInputName(int pos) {
    if (pos >= input.length) return EOF;
    return input[pos].toString();
  }
}
