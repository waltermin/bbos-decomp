package net.rim.ecmascript.runtime;

public class ESNumber extends ESObject {
   private double _value;

   public ESNumber(double value) {
      super("Number", GlobalObject.getInstance().numberPrototype);
      this._value = value;
   }

   ESNumber(double value, boolean nullPrototypeOK) {
      super("Number", GlobalObject.getInstance().numberPrototype, nullPrototypeOK);
      this._value = value;
   }

   public double getValue() {
      return this._value;
   }
}
