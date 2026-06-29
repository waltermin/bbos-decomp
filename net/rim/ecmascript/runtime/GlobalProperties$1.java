package net.rim.ecmascript.runtime;

class GlobalProperties$1 extends HostFunction {
   GlobalProperties$1(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      GlobalObject global = this.getGlobalInstance();
      int nParms = this.getNumParms();
      if (nParms > 0) {
         int i = 0;

         while (true) {
            global.stdOut.print(Convert.toString(this.getParm(i)));
            if (++i >= nParms) {
               global.stdOut.println();
               break;
            }

            global.stdOut.print(" ");
         }
      }

      return Value.UNDEFINED;
   }
}
