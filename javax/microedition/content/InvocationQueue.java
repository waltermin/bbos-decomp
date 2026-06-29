package javax.microedition.content;

import java.util.Vector;

class InvocationQueue extends Vector {
   public InvocationQueue() {
   }

   synchronized void addInvocation(Invocation invocation) {
      this.addElement(invocation);
   }

   synchronized Invocation nextInvocation() {
      Invocation next = null;
      if (this.size() > 0) {
         next = (Invocation)this.elementAt(0);
         this.removeElementAt(0);
      }

      return next;
   }
}
