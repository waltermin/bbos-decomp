package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class SendHostFunction extends JavaScriptHostFunction {
   public SendHostFunction() {
      super(Names.XMLHttpRequest, "send", 1);
   }

   @Override
   public final long run() {
      ((ESXMLHttpRequest)this.getThis()).send(null);
      return Value.DEFAULT;
   }
}
