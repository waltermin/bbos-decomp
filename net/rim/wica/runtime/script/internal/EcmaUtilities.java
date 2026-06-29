package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;

public final class EcmaUtilities {
   public static final void throwESError(String message) {
      throw new Object(Value.makeObjectValue((ESObject)(new Object(message))));
   }

   public static final void throwESError(String clazz, String message) {
      StringBuffer errorStr = (StringBuffer)(new Object());
      if (clazz != null) {
         errorStr.append(clazz);
         errorStr.append(": ");
      }

      errorStr.append(message);
      throwESError(errorStr.toString());
   }

   public static final long makeStringValue(Object value) {
      return !(value instanceof Object) ? Value.NULL : Value.makeStringValue((String)value);
   }
}
