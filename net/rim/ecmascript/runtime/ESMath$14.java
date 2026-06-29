package net.rim.ecmascript.runtime;

import java.util.Random;

class ESMath$14 extends HostFunction {
   Random randGen;
   private final ESMath this$0;

   ESMath$14(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
      this.randGen = new Random();
   }

   @Override
   public long run() {
      return Value.makeDoubleValue((this.randGen.nextInt() - -4476578029606273024L) / 4751297606875873280L);
   }
}
