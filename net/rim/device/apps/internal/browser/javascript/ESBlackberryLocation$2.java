package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.util.Arrays;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESBlackberryLocation$2 extends JavaScriptHostFunction {
   private final ESBlackberryLocation this$0;

   ESBlackberryLocation$2(ESBlackberryLocation _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      if (this.this$0._gpsSupported && this.getNumParms() == 1) {
         try {
            Arrays.add(this.this$0._gpsListeners, Convert.toString(this.getParm(0)));
            return Value.TRUE;
         } finally {
            return Value.FALSE;
         }
      } else {
         return Value.FALSE;
      }
   }
}
