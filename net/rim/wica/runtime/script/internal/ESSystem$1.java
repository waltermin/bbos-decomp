package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

class ESSystem$1 extends HostFunction {
   private final ESSystem this$0;

   ESSystem$1(ESSystem this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      if (this.getNumParms() > 0) {
         try {
            Thread.sleep((long)Convert.toDouble(this.getParm(0)));
         } finally {
            return Value.DEFAULT;
         }
      }

      return Value.DEFAULT;
   }
}
