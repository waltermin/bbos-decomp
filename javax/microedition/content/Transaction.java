package javax.microedition.content;

import java.util.Hashtable;

class Transaction extends Hashtable {
   private Invocation _curr;

   Transaction(Invocation invocation, RegistryImpl registry) {
      this.put(invocation, registry);
      this._curr = invocation;
   }

   synchronized Invocation getActiveInvocation() {
      return this._curr;
   }

   synchronized void append(Invocation invocation, RegistryImpl registry) {
      this.put(invocation, registry);
      this._curr = invocation;
   }

   synchronized void remove(Invocation key) {
      super.remove(key);
      Invocation original = key.getOriginal();
      this._curr = original == null ? null : original.getPrevious();
   }

   synchronized void replaceActive(Invocation newInvocation) {
      RegistryImpl ri = (RegistryImpl)this.get(this._curr);
      super.remove(this._curr);
      this.put(newInvocation, ri);
      this._curr = newInvocation;
   }
}
