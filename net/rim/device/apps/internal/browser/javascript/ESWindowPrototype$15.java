package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.StopEvent;
import net.rim.ecmascript.runtime.Value;

class ESWindowPrototype$15 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$15(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      BrowserContent content = JavaScriptEngine.getInstance()._browserContent;
      RenderingApplication renderingApplication = content.getRenderingApplication();
      if (renderingApplication != null) {
         renderingApplication.eventOccurred(new StopEvent(content));
      }

      return Value.NULL;
   }
}
