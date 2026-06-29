package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESDatePrototype$29 extends ESDatePrototype$ESDateFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$29(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      try {
         ESDate date = (ESDate)this.getThis();
         date.setValue(ESDatePrototype.timeClip(Convert.toDouble(this.getParm(0))));
         return Value.makeDoubleValue(date.getValue());
      } finally {
         throw ThrownValue.typeError(Resources.getString(50), this.getName());
      }
   }
}
