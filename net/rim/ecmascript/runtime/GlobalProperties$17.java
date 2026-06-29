package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class GlobalProperties$17 extends HostFunction {
   GlobalProperties$17(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      ESObject obj = Value.checkIfObjectValue(this.getParm(0));
      if (obj != null && obj instanceof JavaClass) {
         return Value.makeObjectValue(new JavaObjectArray((JavaClass)obj, JavaArray.toIndex(this.getParm(1))));
      } else {
         throw ThrownValue.typeError(Resources.getString(55));
      }
   }
}
