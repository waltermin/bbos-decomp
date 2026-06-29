package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

final class ESHTMLFormElement$ElementsToStringHostFunction extends JavaScriptHostFunction {
   public ESHTMLFormElement$ElementsToStringHostFunction() {
      super(Names.elements, Names.toString);
   }

   @Override
   public final long run() {
      return Value.makeStringValue("[object HTMLFormControlList]");
   }
}
