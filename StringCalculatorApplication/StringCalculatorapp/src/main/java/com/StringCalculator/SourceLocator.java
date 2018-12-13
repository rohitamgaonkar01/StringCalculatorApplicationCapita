
package com.StringCalculator;

import com.StringCalculator.error.Location;
import com.StringCalculator.internal.annotations.Private;
import com.StringCalculator.internal.util.IntList;

/**
 * Locates the line and column number of a 0-based index in the source.
 * 
 * <p> This class internally keeps a cache of the indices of all the line break characters scanned
 * so far, therefore repeated location lookup can be done in amortized log(n) time.
 * 
 * <p> It is <EM>not</EM> multi-thread safe.
 * 
 * @author Rohit Amgaonkar
 */
final class SourceLocator {
  
  /** The line break character. */
  private static final char LINE_BREAK = '\n';
  
  private final CharSequence source;
  
  /** The 0-based indices of the line break characters scanned so far. */
  @Private
  final IntList lineBreakIndices = new IntList(20);
  
  /** The first line number. */
  private final int startLineNumber;
  
  /** The first column number. */
  private final int startColumnNumber;
  
  /** The 0-based index of the next character to be scanned. */
  @Private int nextIndex = 0;
  
  /** The 0-based index of the column of the next character to be scanned. */
  @Private int nextColumnIndex = 0;
  
  /**
   * Creates a { SourceLocator} object.
   * 
   * @param source the source.
   */  
  SourceLocator(CharSequence source) {
    this(source, 1, 1);
  }
  
  /**
   * Creates a { SourceLocator} object.
   * 
   * @param source the source.
   * @param lineNumber the starting line number.
   * @param columnNumber the starting column number.
   */
  @Private SourceLocator(CharSequence source, int lineNumber, int columnNumber) {
    this.source = source;
    this.startLineNumber = lineNumber;
    this.startColumnNumber = columnNumber;
  }
  
  Location locate(int index) {
    return (index < nextIndex) ? lookup(index) : scanTo(index);
  }
  
  /**
   * Looks up the location identified by {@code ind} using the cached indices of line break
   * characters. This assumes that all line-break characters before {@code ind} are already scanned.
   */
  @Private Location lookup(int index) {
    int size = lineBreakIndices.size();
    if (size == 0) return location(0, index);
    int lineNumber = binarySearch(lineBreakIndices, index);
    if (lineNumber == 0) return location(0, index);
    int previousBreak = lineBreakIndices.get(lineNumber - 1);
    return location(lineNumber, index - previousBreak - 1);
  }
  
  /**
   * Scans from {@code nextIndex} to {@code ind} and saves all indices of line break characters
   * into {@code lineBreakIndices} and adjusts the current column number as it goes. The location of
   * the character on {@code ind} is returned.
   * 
   * <p> After this method returns, {@code nextIndex} and {@code nextColumnIndex} will point to the
   * next character to be scanned or the EOF if the end of input is encountered.
   */
  @Private Location scanTo(int index) {
    boolean eof = false;
    if (index == source.length()) { // The eof has index size() + 1
      eof = true;
      index--;
    }
    int columnIndex = nextColumnIndex;
    for (int i = nextIndex; i <= index; i++) {
      char c = source.charAt(i);
      if (c == LINE_BREAK) {
        lineBreakIndices.add(i);
        columnIndex = 0;
      }
      else columnIndex++;
    }
    this.nextIndex = index + 1;
    this.nextColumnIndex = columnIndex;
    int lines = lineBreakIndices.size();
    if (eof) return location(lines, columnIndex);
    if (columnIndex == 0) return getLineBreakLocation(lines - 1);
    return location(lines, columnIndex - 1);
  }
  
  /**
   * Gets the 0-based column number of the line break character for line identified by
   * {@code lineIndex}.
   */
  private int getLineBreakColumnIndex(int lineIndex) {
    int lineBreakIndex = lineBreakIndices.get(lineIndex);
    return (lineIndex == 0) ?
         lineBreakIndex : lineBreakIndex - lineBreakIndices.get(lineIndex - 1) - 1;
  }
  
  private Location getLineBreakLocation(int lineIndex) {
    return location(lineIndex, getLineBreakColumnIndex(lineIndex));
  }
  
  private Location location(int l, int c) {
    return new Location(startLineNumber + l, (l == 0 ? startColumnNumber : 1) + c);
  }
  
  /**
   * Uses binary search to look up the index of the first element in {@code ascendingInts} that's
   * greater than or equal to {@code value}. If all elements are smaller than {@code value},
   * {@code ascendingInts.size()} is returned.
   */
  @Private static int binarySearch(IntList ascendingInts, int value) {
    for (int begin = 0, to = ascendingInts.size();;) {
      if (begin == to) return begin;
      int i = (begin + to) / 2;
      int x = ascendingInts.get(i);
      if (x == value) return i;
      else if (x > value) to = i;
      else begin = i + 1;
    }
  }
}
