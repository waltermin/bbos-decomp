package net.rim.ecmascript.runtime;

public class ESBoolean extends ESObject {
   private boolean _value;

   ESBoolean(boolean value, boolean nullPrototypeOK) {
      super("Boolean", GlobalObject.getInstance().booleanPrototype, nullPrototypeOK);
      this._value = value;
   }

   public ESBoolean(boolean value) {
      super("Boolean", GlobalObject.getInstance().booleanPrototype);
      this._value = value;
   }

   public boolean getValue() {
      return this._value;
   }
}
