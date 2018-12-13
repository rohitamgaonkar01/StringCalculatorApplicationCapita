
package com.StringCalculator.error;

import java.util.List;

import com.StringCalculator.Parsers;

/**
 * Describes details of a parsing error to support fine-grained error handling.
 * 
 * @author Rohit Amgaonkar
 */
public interface ParseErrorDetails {
  
  /** Returns the 0-based index in the source where the error happened. */
  int getIndex();
  
  /** Returns the physical input encountered when the error happened. */
  String getEncountered();
  
  /** Returns all that are logically expected. */
  List<String> getExpected();
  
  /** Returns what is logically unexpected, or {@code null} if none. */
  String getUnexpected();
  
  /**
   * Returns the error message incurred by { Parsers#fail(String)},
   * or {@code null} if none.
   */
  String getFailureMessage();
}
