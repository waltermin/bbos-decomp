package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class AbortHostFunction extends JavaScriptHostFunction {
   public AbortHostFunction() {
      super(Names.XMLHttpRequest, "abort", 0);
   }

   @Override
   public final long run() {
      ((ESXMLHttpRequest)this.getThis()).abort();
      return Value.DEFAULT;
   }
}
