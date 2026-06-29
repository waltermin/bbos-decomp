package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.util.internal.RuntimeUtilities;

class ESSystem$2 extends HostFunction {
   private final ESSystem this$0;

   ESSystem$2(ESSystem this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      return Value.makeBooleanValue(RuntimeUtilities.validateRuntimeCompatibility(Value.getStringValue(this.getParm(0))));
   }
}
