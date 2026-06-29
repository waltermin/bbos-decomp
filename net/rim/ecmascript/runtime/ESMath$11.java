package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESMath$11 extends HostFunction {
   private final ESMath this$0;

   ESMath$11(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double max = (double)-4503599627370496L;
      int nParms = this.getNumParms();

      for (int i = 0; i < nParms; i++) {
         double d = Convert.toDouble(this.getParm(i));
         if (Double.isNaN(d)) {
            max = (double)9221120237041090560L;
            break;
         }

         if (Misc.compareDouble(d, max) > 0) {
            max = d;
         }
      }

      return Value.makeDoubleValue(max);
   }
}
