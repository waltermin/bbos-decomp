package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

final class ESApplication$SetIntervalFunction extends HostFunction {
   private final ESApplication this$0;

   ESApplication$SetIntervalFunction(ESApplication this$0) {
      super("application", "setInterval", 2);
      this.this$0 = this$0;
   }

   @Override
   public final long run() {
      int numParms = this.getNumParms();
      if (numParms <= 1) {
         return Value.makeIntegerValue(0);
      }

      ESObject action = Convert.toObject(this.getParm(0));
      long timeout = (long)Convert.toDouble(this.getParm(1));
      long[] args = null;
      if (numParms > 2) {
         args = new long[numParms - 2];

         for (int i = 2; i < numParms; i++) {
            args[i - 2] = this.getParm(i);
         }
      }

      return Value.makeIntegerValue(this.this$0._engine.getTimer().setTimeout(action, timeout, args, true));
   }
}
