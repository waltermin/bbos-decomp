package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class GetAllRequestHeadersHostFunction extends JavaScriptHostFunction {
   public GetAllRequestHeadersHostFunction() {
      super(Names.XMLHttpRequest, "getAllResponseHeaders", 0);
   }

   @Override
   public final long run() {
      String value = ((ESXMLHttpRequest)this.getThis()).getAllResponseHeaders();
      return value == null ? Value.NULL : JavaScriptEngine.makeStringValue(value);
   }
}
