package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.DOMException;

final class ESDOMException extends ESObject {
   private ESDOMException(int code) {
      super(Names.DOMException, JavaScriptEngine.getInstance()._domExceptionPrototype);
      this.addField(Names.code, 7, Value.makeIntegerValue(code));
   }

   static final ThrownValue createThrownValue(int code) {
      return (ThrownValue)(new Object(Value.makeObjectValue(new ESDOMException(code))));
   }

   static final ThrownValue createThrownValue(DOMException e) {
      return (ThrownValue)(new Object(Value.makeObjectValue(new ESDOMException(e.code))));
   }
}
