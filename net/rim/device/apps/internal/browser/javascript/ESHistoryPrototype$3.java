package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.HistoryEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESHistoryPrototype$3 extends JavaScriptHostFunction {
   private final ESHistoryPrototype this$0;

   ESHistoryPrototype$3(ESHistoryPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      BrowserContent content = JavaScriptEngine.getInstance()._browserContent;
      RenderingApplication renderingApplication = content.getRenderingApplication();
      if (renderingApplication != null) {
         try {
            int index = Convert.toInt32(this.getParm(0, Value.makeIntegerValue(0)));
            renderingApplication.eventOccurred(new HistoryEvent(content, index, true, 0));
         } finally {
            return Value.NULL;
         }
      }

      return Value.NULL;
   }
}
