package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class ESWindow$ToStringHostFunction extends JavaScriptHostFunction {
   public ESWindow$ToStringHostFunction() {
      super(Names.Window, Names.toString);
   }

   @Override
   public final long run() {
      return Value.makeStringValue("[object Window]");
   }
}
