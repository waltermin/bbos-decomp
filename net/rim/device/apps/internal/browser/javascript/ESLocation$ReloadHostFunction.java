package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class ESLocation$ReloadHostFunction extends JavaScriptHostFunction {
   private final ESLocation this$0;

   public ESLocation$ReloadHostFunction(ESLocation _1) {
      super(Names.Location, Names.reload);
      this.this$0 = _1;
   }

   @Override
   public final long run() {
      String url = this.this$0._domDoc.getURL();
      if (url != null) {
         this.this$0.redirect(url);
      }

      return Value.UNDEFINED;
   }
}
