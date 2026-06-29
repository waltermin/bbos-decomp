package com.fourthpass.wmls;

final class IntegerValue extends Value {
   private int _value;
   static final IntegerValue ONE = new IntegerValue(1);
   static final IntegerValue ZERO = new IntegerValue(0);
   static final IntegerValue MINUS_ONE = new IntegerValue(-1);

   public IntegerValue(int i) {
      this._value = i;
   }

   @Override
   public final Value Add(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         long d = this._value;
         d += ((IntegerValue)value).getValue();
         if (d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE) {
            this._value = (int)d;
            return this;
         } else {
            return Value.INVALID;
         }
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value BAnd(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value & ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value BLShift(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value << ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value BNot() {
      this._value = ~this._value;
      return this;
   }

   @Override
   public final Value BOr(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value | ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value BRShift(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value >> ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value BRSZShift(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value >>> ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value BXOr(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value ^ ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Decr() {
      if (this._value <= Integer.MIN_VALUE) {
         return Value.INVALID;
      }

      this._value--;
      return this;
   }

   @Override
   public final Value Div(Value value) {
      return Value.INVALID;
   }

   @Override
   public final Value Eq(Value value) {
      Value integer = value.toIntegerValue();
      return integer.isInteger() && this._value == ((IntegerValue)integer).getValue() ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value GE(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value >= ((IntegerValue)value).getValue() ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value GT(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value > ((IntegerValue)value).getValue() ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value IDiv(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value / ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Incr() {
      if (this._value >= Integer.MAX_VALUE) {
         return Value.INVALID;
      }

      this._value++;
      return this;
   }

   @Override
   public final Value LE(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value <= ((IntegerValue)value).getValue() ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value LT(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value < ((IntegerValue)value).getValue() ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value Mul(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         long d = this._value;
         d *= ((IntegerValue)value).getValue();
         if (d <= Integer.MAX_VALUE && d >= Integer.MIN_VALUE) {
            this._value = (int)d;
            return this;
         } else {
            return Value.INVALID;
         }
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value NE(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value != ((IntegerValue)value).getValue() ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value Rem(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         this._value = this._value % ((IntegerValue)value).getValue();
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Sub(Value value) {
      Value val = value.toIntegerValue();
      if (val.isInteger()) {
         int intValue = ((IntegerValue)value).getValue();
         long d = this._value - intValue;
         this._value -= intValue;
         return d < Integer.MIN_VALUE ? Value.INVALID : this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value UMinus() {
      this._value = -this._value;
      return this;
   }

   @Override
   public final Value _int() {
      return this.clone();
   }

   @Override
   public final Value abs() {
      return new IntegerValue(Math.abs(this._value));
   }

   @Override
   public final Value clone() {
      return new IntegerValue(this._value);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof IntegerValue ? this._value == ((IntegerValue)obj).getValue() : false;
   }

   public final int getValue() {
      return this._value;
   }

   @Override
   public final Value max(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value >= ((IntegerValue)val).getValue() ? this.clone() : value.clone();
   }

   @Override
   public final Value min(Value value) {
      Value val = value.toIntegerValue();
      return val.isInteger() && this._value <= ((IntegerValue)val).getValue() ? this.clone() : value.clone();
   }

   @Override
   public final Value toBooleanValue() {
      return this._value != 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value toFloatValue() {
      return new FloatValue(this._value);
   }

   @Override
   public final Value toIntegerValue() {
      return this;
   }

   @Override
   public final String toString() {
      return ((StringValue)this.toStringValue()).getString();
   }

   @Override
   public final Value toStringValue() {
      return new StringValue(Integer.toString(this._value));
   }

   @Override
   public final int typeOf() {
      return 0;
   }

   @Override
   public final Value parseInt() {
      return this.clone();
   }
}
