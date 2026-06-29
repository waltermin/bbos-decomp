package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;

class ESWindowPrototype$14 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$14(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      ESObject windowObj = thiz;
      if (thiz instanceof GlobalObject) {
         windowObj = ((GlobalObject)thiz).getRedirectionObject();
      }

      if (windowObj instanceof ESWindow) {
         int count = this.getNumParms();
         if (count >= 2) {
            try {
               String method = Convert.toString(this.getParm(0));
               int timeout = Convert.toInt32(this.getParm(1));
               return Value.makeIntegerValue(((ESWindow)windowObj)._timer.enque(method, timeout, false));
            } finally {
               return Value.NULL;
            }
         }
      }

      return Value.NULL;
   }
}
