
package com.StringCalculator.error;

import java.util.LinkedHashSet;
import java.util.List;

import com.StringCalculator.internal.annotations.Private;


final class ErrorReporter {
  
  static String toString(ParseErrorDetails details, Location location) {
    StringBuilder buf = new StringBuilder();
    if (location != null) {
      buf.append("line " + location.line + ", column " + location.column);
    }
    if (details != null) {
      //buf.append(":\n");
      if (details.getFailureMessage() != null) {
        
      }
      else if (!details.getExpected().isEmpty()) {
        reportList(buf, details.getExpected());
        
        System.out.println(" INVALID EXPRESSION");
      }
      else if (details.getUnexpected() != null) {
        buf.append("unexpected ").append(details.getUnexpected()).append('.');
      }
    }
    return buf.toString();
  }
  
  @Private static void reportList(StringBuilder builder, List<String> messages) {
    if (messages.isEmpty()) return;
    LinkedHashSet<String> set = new LinkedHashSet<String>(messages);
    int size = set.size();
    int i = 0;
    for (String message : set) {
      if (i++ > 0) {
        if (i == size) { // last one
          builder.append(" or ");
        } else {
          builder.append(", ");
        }
      }
      builder.append(message);
    }
  }
}
