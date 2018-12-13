
package com.StringCalculator;


enum IntOrder {
  

  LT {
    @Override public boolean compare(int a, int b) { return a < b; }
    @Override public String toString() {
      return "shortest";
    }
  },
  
  
  GT {
    @Override public boolean compare(int a, int b) {return a > b;}
    @Override public String toString() {
      return "longest";
    }
  }

  ;
  
  abstract boolean compare(int a, int b);
}
