package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;

class ESSystem$6 extends HostFunction {
   private final ESSystem this$0;

   ESSystem$6(ESSystem this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      int numParams = this.getNumParms();
      String resourceName = null;
      if (numParams != 1) {
         EcmaUtilities.throwESError(this.getObjectClass(), RuntimeResources.getString(135, "System.play()"));
      }

      boolean var5 = false /* VF: Semaphore variable */;

      label22:
      try {
         var5 = true;
         resourceName = ((ESString)Convert.toObject(this.getParm(0))).getValue();
         var5 = false;
      } finally {
         if (var5) {
            EcmaUtilities.throwESError(this.getObjectClass(), RuntimeResources.getString(139));
            break label22;
         }
      }

      this.this$0._context.getAccessServ().play(resourceName);
      return Value.NULL;
   }
}
