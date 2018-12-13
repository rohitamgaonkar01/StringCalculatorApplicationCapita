
package com.StringCalculator.internal.util;

import com.StringCalculator.internal.annotations.Private;

/**
 * A simple, efficient and dynamic int list.
 * 
 * <p> Not thread-safe.
 * 
 * @author Rohit Amgaonkar.
 */
public final class IntList {
  private int[] buf;
  private int len = 0;
  
  /** Creates a {@code int[]} object with all the elements. */
  public int[] toArray() {
    int[] ret = new int[len];
    for(int i = 0; i < len; i++) {
      ret[i] = buf[i];
    }
    return ret;
  }
  
  /** Creates an { IntList} object with initial capacity equal to {@code capacity}. */
  public IntList(int capacity) {
    this.buf = new int[capacity];
  }
  
  /** Creates an empty { IntList} object. */
  public IntList() {
    this(10);
  }
  
  /** Gets the number of int values stored. */
  public int size() {
    return len;
  }
  
  private void checkIndex(int i) {
    if (i < 0 || i >= len)
      throw new ArrayIndexOutOfBoundsException(i);
  }
  
  /**
   * Gets the int value at a index {@code i}.
   * @param i the 0 - based index of the value. 
   * @return the int value.
   * @throws ArrayIndexOutOfBoundsException if {@code i &lt; 0 or i >= size()}.
   */
  public int get(int i) {
    checkIndex(i);
    return buf[i];
  }
  
  /**
   * Sets the value at index {@code i} to {@code val}.
   * 
   * @param i the 0 - based index.
   * @param val the new value.
   * @return the old value.
   * @throws ArrayIndexOutOfBoundsException if {@code i &lt; 0 or i >= size()}.
   */
  public int set(int i, int val) {
    checkIndex(i);
    int old = buf[i];
    buf[i] = val;
    return old;
  }
  
  @Private static int calcSize(int expectedSize, int factor) {
    int rem = expectedSize % factor;
    return expectedSize / factor * factor + (rem > 0 ? factor : 0);
  }
  
  /**
   * Ensures that there is at least {@code l} capacity.
   * 
   * @param capacity the minimal capacity.
   */
  public void ensureCapacity(int capacity) {
    if (capacity > buf.length) {
      int factor = buf.length / 2 + 1;
      grow(calcSize(capacity - buf.length, factor));
    }
  }
  
  private void grow(int l) {
    int[] nbuf = new int[buf.length + l];
    System.arraycopy(buf, 0, nbuf, 0, buf.length);
    buf = nbuf;
  }
  
  /**
   * Adds {@code i} into the array.
   * 
   * @param i the int value.
   * @return this object.
   */
  public IntList add(int i) {
    ensureCapacity(len + 1);
    buf[len++] = i;
    return this;
  }
}
