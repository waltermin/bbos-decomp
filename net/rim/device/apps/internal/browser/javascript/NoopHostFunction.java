package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class NoopHostFunction extends JavaScriptHostFunction {
   public NoopHostFunction(String clazz, String name) {
      super(clazz, name);
   }

   @Override
   public final long run() {
      return Value.UNDEFINED;
   }
}
