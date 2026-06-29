package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;

class ESSystem$9 extends HostFunction {
   private final ESSystem this$0;

   ESSystem$9(ESSystem this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      int numParams = this.getNumParms();
      if (numParams != 1) {
         EcmaUtilities.throwESError(this.getObjectClass(), RuntimeResources.getString(135, "System.getProperty()"));
      }

      try {
         return EcmaUtilities.makeStringValue(
            this.this$0._context.getWiclet().getContext().getProperty(((ESString)Convert.toObject(this.getParm(0))).getValue())
         );
      } finally {
         EcmaUtilities.throwESError(this.getObjectClass(), "Illegal argument for System.getProperty()");
         return Value.NULL;
      }
   }
}
