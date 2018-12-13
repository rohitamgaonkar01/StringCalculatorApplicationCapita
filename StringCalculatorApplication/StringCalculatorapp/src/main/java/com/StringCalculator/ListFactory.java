
package com.StringCalculator;

import java.util.ArrayList;
import java.util.List;

import com.StringCalculator.internal.util.Lists;


abstract class ListFactory<T> {
  
  abstract List<T> newList();
  
  @SuppressWarnings("unchecked")
  static <T> ListFactory<T> arrayListFactory() {
    return ARRAY_LIST_FACTORY;
  }
  
  static <T> ListFactory<T> arrayListFactoryWithFirstElement(final T first) {
    return new ListFactory<T>() {
      @Override List<T> newList() {
        ArrayList<T> list = Lists.arrayList();
        list.add(first);
        return list;
      }
    };
  }
  @SuppressWarnings("rawtypes")
  private static final ListFactory ARRAY_LIST_FACTORY = new ListFactory<Object>() {
    @Override List<Object> newList() {
      return Lists.arrayList();
    }
  };
}
