package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class GlobalProperties$12 extends HostFunction {
   GlobalProperties$12(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      long parm = this.getParm(0);

      try {
         ESObject obj = Value.checkIfObjectValue(parm);
         if (obj != null) {
            ((JavaClass)obj).export(this.getGlobalInstance());
            return Value.UNDEFINED;
         }
      } finally {
         throw ThrownValue.typeError(Resources.getString(46), Convert.toString(parm));
      }

      throw ThrownValue.typeError(Resources.getString(46), Convert.toString(parm));
   }
}
