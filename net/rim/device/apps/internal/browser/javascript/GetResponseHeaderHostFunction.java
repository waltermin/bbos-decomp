package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

final class GetResponseHeaderHostFunction extends JavaScriptHostFunction {
   public GetResponseHeaderHostFunction() {
      super(Names.XMLHttpRequest, "getResponseHeader", 1);
   }

   @Override
   public final long run() {
      String header = Convert.toString(this.getParm(0));
      return JavaScriptEngine.makeStringValue(((ESXMLHttpRequest)this.getThis()).getResponseHeader(header));
   }
}
