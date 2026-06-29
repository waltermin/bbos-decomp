package net.rim.wica.runtime.provisioning.internal.digester;

import java.util.Stack;

public class PeekableStack extends Stack {
   public synchronized Object peek(int n) {
      int len = this.size();
      if (len == 0) {
         throw new Object();
      } else {
         int pos = len - 1 - n;
         if (pos >= 0 && n >= 0) {
            return this.elementAt(pos);
         } else {
            throw new Object(((StringBuffer)(new Object("Can not peek at "))).append(n).append("th element when stack size is ").append(len).toString());
         }
      }
   }

   public synchronized void clear() {
      this.removeAllElements();
   }
}
