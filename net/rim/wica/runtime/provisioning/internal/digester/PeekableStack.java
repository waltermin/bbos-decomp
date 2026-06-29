package net.rim.wica.runtime.provisioning.internal.digester;

import java.util.EmptyStackException;
import java.util.Stack;

public class PeekableStack extends Stack {
   public synchronized Object peek(int n) {
      int len = this.size();
      if (len == 0) {
         throw new EmptyStackException();
      } else {
         int pos = len - 1 - n;
         if (pos >= 0 && n >= 0) {
            return this.elementAt(pos);
         } else {
            throw new IllegalArgumentException("Can not peek at " + n + "th element when stack size is " + len);
         }
      }
   }

   public synchronized void clear() {
      this.removeAllElements();
   }
}
