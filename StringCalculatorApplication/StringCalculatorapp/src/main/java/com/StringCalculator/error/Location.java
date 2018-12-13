
package com.StringCalculator.error;


public final class Location {
  
  /** 1-based line number. */
  public final int line;
  
  /** 1-based column number. */
  public final int column;
  

  public Location(int line, int column) {
    this.line = line;
    this.column = column;
  }
  
  @Override public boolean equals(Object obj) {
    if (obj instanceof Location) {
      Location other = (Location) obj;
      return line == other.line && column == other.column;
    }
    return false;
  }

  @Override public int hashCode() {
    return line * 31 + column;
  }

  @Override public String toString() {
    return "line " + line + " column "+column;
  }
}
