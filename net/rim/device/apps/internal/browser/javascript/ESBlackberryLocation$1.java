package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

class ESBlackberryLocation$1 extends JavaScriptHostFunction {
   private final ESBlackberryLocation this$0;

   ESBlackberryLocation$1(ESBlackberryLocation _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      if (this.this$0._gpsSupported) {
         if (!this.this$0._listenerRegistered) {
            try {
               this.this$0._locationProvider.setLocationListener(this.this$0, 10, 10, 10);
            } finally {
               ;
            }

            this.this$0._listenerRegistered = true;
         }

         return Value.TRUE;
      } else {
         return Value.FALSE;
      }
   }
}
