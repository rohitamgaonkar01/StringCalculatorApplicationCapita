
package com.StringCalculator.error;

import com.StringCalculator.ParseTree;
import com.StringCalculator.Parser;

/**
 * Is thrown when any grammar error happens or any exception is thrown during parsing.
 * 
 * @author Rohit Amgaonkar
 */
@SuppressWarnings("serial")
public class ParserException extends RuntimeException {
  
  private final ParseErrorDetails error;
  private final Location location;
  private ParseTree parseTree = null;
  @Deprecated private final String module;
  
  /**
   * Creates a { ParserException} object.
   * 
   * @param details the { ParseErrorDetails} that describes the error details.
   * @param location the error location.
   */
  public ParserException(ParseErrorDetails details,  Location location) {
    this(details, null, location);
  }
  
  /**
   * Creates a { ParserException} object.
   * 
   * @param details the { ParseErrorDetails} that describes the error details.
   * @param moduleName the module name.
   * @param location the error location.
   */
  @Deprecated
  public ParserException(ParseErrorDetails details,  String moduleName, Location location) {
    super(toErrorMessage(null, moduleName, details, location));
    this.error = details;
    this.module = moduleName;
    this.location = location;
  }

  /**
   * Creates a { ParserException} object.
   * 
   * @param cause the exception that causes this.
   * @param details the { ParseErrorDetails} that describes the error details.
   * @param moduleName the module name.
   * @param location the location.
   */
  @Deprecated
  public ParserException(
      Throwable cause, ParseErrorDetails details, String moduleName, Location location) {
    super(toErrorMessage(cause.getMessage(), moduleName, details, location), cause);
    this.error = details;
    this.location = location;
    this.module = moduleName;
  }

  /**
   * Returns the detailed description of the error, or {@code null} if none.
   */
  public ParseErrorDetails getErrorDetails() {
    return error;
  }

  /**
   * Returns the parse tree until the parse error happened, when 
   * { Parser#parseTree parseTree()} was invoked.
   * {@code null} if absent.
   *
   * @since 2.3
   */
  public ParseTree getParseTree() {
    return parseTree;
  }

  /** @since 2.3 */
  public void setParseTree(ParseTree parseTree) {
    this.parseTree = parseTree;
  }
  
  private static String toErrorMessage(
      String message, String module, ParseErrorDetails details, Location location) {
    StringBuilder buf = new StringBuilder();
    if (message != null && message.length() > 0) {
      buf.append(message).append('\n');
    }
    if (module != null) {
      buf.append('(').append(module).append(") ");
    }
    buf.append(ErrorReporter.toString(details, location));
    return buf.toString();
  }
  
  /** Returns the module name, or {@code null} if none. */
  @Deprecated
  public String getModuleName() {
    return module;
  }
  
  /** Returns the location of the error. */
  public Location getLocation() {
    return location;
  }
}
