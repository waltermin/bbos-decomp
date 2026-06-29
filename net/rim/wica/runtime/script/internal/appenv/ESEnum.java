package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.wica.runtime.script.internal.WicaAppContext;

public final class ESEnum extends ESObject {
   private String _id;
   private int _type;
   private int _value;

   public ESEnum(String id, int type, int value, WicaAppContext context) {
      super("Enumeration", context.getEnumPrototype());
      this._id = id;
      this._type = type;
      this._value = value;
   }

   public final String getId() {
      return this._id;
   }

   public final String getStringValue() {
      return this._id;
   }

   public final int getType() {
      return this._type;
   }

   public final int getValue() {
      return this._value;
   }
}
