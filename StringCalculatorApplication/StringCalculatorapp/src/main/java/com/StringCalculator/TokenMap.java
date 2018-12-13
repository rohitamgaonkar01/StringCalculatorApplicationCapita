
package com.StringCalculator;


/**
 * Maps a { Token} to a an object of type {@code T}, or null if the token isn't recognized.
 * 
 * @author Rohit Amgaonkar
 */
@FunctionalInterface
public interface TokenMap<T> {
  
  /**
   * Transforms {@code token} to an instance of {@code T}.
   * {@code null} is returned if the token isn't recognized.
   */
  T map(Token token);
}
