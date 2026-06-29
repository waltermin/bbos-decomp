package net.rim.ecmascript.runtime;

public class ESDate extends ESObject {
   protected double _value;

   ESDate(double value, boolean nullPrototypeOK) {
      super("Date", GlobalObject.getInstance().datePrototype, true);
      this._value = value;
   }

   public ESDate(double value) {
      super("Date", GlobalObject.getInstance().datePrototype);
      this._value = value;
   }

   public double getValue() {
      return this._value;
   }

   public void setValue(double value) {
      this._value = value;
   }

   @Override
   public long defaultValue() {
      return this.defaultStringValue();
   }
}
