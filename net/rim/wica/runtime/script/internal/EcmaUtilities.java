package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.ESError;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;

public final class EcmaUtilities {
   public static final void throwESError(String message) throws ThrownValue {
      throw new ThrownValue(Value.makeObjectValue(new ESError(message)));
   }

   public static final void throwESError(String clazz, String message) {
      StringBuffer errorStr = new StringBuffer();
      if (clazz != null) {
         errorStr.append(clazz);
         errorStr.append(": ");
      }

      errorStr.append(message);
      throwESError(errorStr.toString());
   }

   public static final long makeStringValue(Object value) {
      return !(value instanceof String) ? Value.NULL : Value.makeStringValue((String)value);
   }
}
