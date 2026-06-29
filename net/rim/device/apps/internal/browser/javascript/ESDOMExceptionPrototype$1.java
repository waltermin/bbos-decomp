package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESDOMExceptionPrototype$1 extends JavaScriptHostFunction {
   private final ESDOMExceptionPrototype this$0;

   ESDOMExceptionPrototype$1(ESDOMExceptionPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int code = Convert.toInt32(((ESDOMException)this.getThis()).getField(Names.code));
      return code >= 1 && code <= ESDOMExceptionPrototype.ERRORS.length
         ? Value.makeStringValue(Names.DOMException + ": " + ESDOMExceptionPrototype.ERRORS[code - 1])
         : Value.makeStringValue(Names.DOMException);
   }
}
