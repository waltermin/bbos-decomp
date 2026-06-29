package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Constructor;
import net.rim.ecmascript.runtime.Value;

final class ESXMLHttpRequestPrototype$Constructor extends Constructor {
   ESXMLHttpRequestPrototype$Constructor() {
      super(Names.XMLHttpRequest, JavaScriptEngine.getInstance()._xmlHttpRequestPrototype);
   }

   @Override
   public final long run() {
      return Value.makeObjectValue(new ESXMLHttpRequest());
   }
}
