package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.resources.RuntimeResources;

class ESLogger$1 extends HostFunction {
   private final ESLogger this$0;

   ESLogger$1(ESLogger this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         String e = Convert.toString(this.getParm(0));
         String message = Convert.toString(this.getParm(1));
         String level = null;
         long levelValue = this.getParm(2);
         if (Value.getType(levelValue) != Value.UNDEFINED) {
            level = Convert.toString(levelValue);
         }

         Logger.log(e, message, this.this$0.getLevelByName(level));
         var7 = false;
      } finally {
         if (var7) {
            EcmaUtilities.throwESError(this.getObjectClass(), RuntimeResources.getString(134));
            return Value.NULL;
         }
      }

      return Value.NULL;
   }
}
