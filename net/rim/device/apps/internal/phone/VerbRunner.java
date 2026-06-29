package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;

class VerbRunner implements Runnable {
   private Verb _verbToInvoke;
   private Object _context = null;

   public VerbRunner(Verb verb) {
      this._verbToInvoke = verb;
   }

   public VerbRunner(Verb verb, Object context) {
      this(verb);
      this._context = context;
   }

   @Override
   public void run() {
      this._verbToInvoke.invoke(this._context);
   }
}
