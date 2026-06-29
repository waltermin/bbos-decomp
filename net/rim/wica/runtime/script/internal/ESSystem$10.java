package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

class ESSystem$10 extends HostFunction {
   private final ESSystem this$0;

   ESSystem$10(ESSystem this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      int numParams = this.getNumParms();
      if (numParams != 2) {
         EcmaUtilities.throwESError(this.getObjectClass(), "Illegal argument for System.setProperty()");
      }

      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         String e = ((ESString)Convert.toObject(this.getParm(0))).getValue();
         String propValue = Convert.toString(this.getParm(1));
         this.this$0._context.getWiclet().getContext().setProperty(e, propValue);
         var5 = false;
      } finally {
         if (var5) {
            EcmaUtilities.throwESError(this.getObjectClass(), "Illegal argument for System.setProperty()");
            return Value.NULL;
         }
      }

      return Value.NULL;
   }
}
