package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class GlobalProperties$11 extends HostFunction {
   GlobalProperties$11(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      long parm = this.getParm(0);

      try {
         ESObject obj = Value.checkIfObjectValue(parm);
         if (obj != null) {
            this.getGlobalInstance().importPackage((JavaPackage)obj);
            return Value.UNDEFINED;
         }
      } finally {
         throw ThrownValue.typeError(Resources.getString(47), Convert.toString(parm));
      }

      throw ThrownValue.typeError(Resources.getString(47), Convert.toString(parm));
   }
}
