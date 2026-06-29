package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESMath$12 extends HostFunction {
   private final ESMath this$0;

   ESMath$12(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double min = (double)9218868437227405312L;
      int nParms = this.getNumParms();

      for (int i = 0; i < nParms; i++) {
         double d = Convert.toDouble(this.getParm(i));
         if (Double.isNaN(d)) {
            min = (double)9221120237041090560L;
            break;
         }

         if (Misc.compareDouble(d, min) < 0) {
            min = d;
         }
      }

      return Value.makeDoubleValue(min);
   }
}
