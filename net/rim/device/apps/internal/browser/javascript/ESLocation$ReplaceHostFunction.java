package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

final class ESLocation$ReplaceHostFunction extends JavaScriptHostFunction {
   private final ESLocation this$0;

   public ESLocation$ReplaceHostFunction(ESLocation _1) {
      super(Names.Location, Names.replace);
      this.this$0 = _1;
   }

   @Override
   public final long run() {
      int count = this.getNumParms();
      if (count == 1) {
         try {
            this.this$0.redirect(Convert.toString(this.getParm(0)));
         } finally {
            return Value.UNDEFINED;
         }
      }

      return Value.UNDEFINED;
   }
}
