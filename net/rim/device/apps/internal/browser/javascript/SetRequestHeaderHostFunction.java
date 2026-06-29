package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

final class SetRequestHeaderHostFunction extends JavaScriptHostFunction {
   public SetRequestHeaderHostFunction() {
      super(Names.XMLHttpRequest, "setRequestHeader", 2);
   }

   @Override
   public final long run() {
      String header = Convert.toString(this.getParm(0));
      String value = Convert.toString(this.getParm(1));
      ((ESXMLHttpRequest)this.getThis()).setRequestHeader(header, value);
      return Value.DEFAULT;
   }
}
