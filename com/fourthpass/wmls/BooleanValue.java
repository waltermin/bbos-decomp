package com.fourthpass.wmls;

final class BooleanValue extends Value {
   private boolean _value;
   public static final BooleanValue FALSE = new BooleanValue(false);
   public static final BooleanValue TRUE = new BooleanValue(true);

   public BooleanValue(boolean value) {
      this._value = value;
   }

   @Override
   public final Value Not() {
      return this._value ? FALSE : TRUE;
   }

   public final boolean getValue() {
      return this._value;
   }

   @Override
   public final Value toBooleanValue() {
      return this;
   }

   @Override
   public final Value toIntegerValue() {
      return new IntegerValue(this._value ? 1 : 0);
   }

   @Override
   public final Value toFloatValue() {
      return new FloatValue((float)(this._value ? 1065353216 : 0));
   }

   @Override
   public final String toString() {
      return this._value ? "true" : "false";
   }

   @Override
   public final Value toStringValue() {
      return this._value ? new StringValue("true") : new StringValue("false");
   }

   @Override
   public final int typeOf() {
      return 3;
   }

   @Override
   public final Value clone() {
      return new BooleanValue(this._value);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof BooleanValue ? this._value == ((BooleanValue)obj).getValue() : false;
   }
}
