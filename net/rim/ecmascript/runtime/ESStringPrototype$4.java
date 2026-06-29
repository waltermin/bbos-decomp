package net.rim.ecmascript.runtime;

import net.rim.device.api.util.StringUtilities;

class ESStringPrototype$4 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$4(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = ESStringPrototype.toString(this.getThis());
      str = StringUtilities.toUpperCase(str, 1701707776);
      return Value.makeStringValue(str);
   }
}
