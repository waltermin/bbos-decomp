package com.fourthpass.wmls;

final class FloatValue extends Value {
   private float _value;

   public FloatValue(float value) {
      this._value = value;
   }

   @Override
   public final int typeOf() {
      return 1;
   }

   public final float getValue() {
      return this._value;
   }

   public final Value toInt() {
      return new IntegerValue((int)this._value);
   }

   @Override
   public final Value toFloatValue() {
      return this;
   }

   public final Value floor() {
      return new IntegerValue((int)Math.floor(this._value));
   }

   public final Value ceil() {
      return new IntegerValue((int)Math.ceil(this._value));
   }

   public final Value round() {
      return new IntegerValue((int)(this._value + 1056964608));
   }

   public final Value sqrt() {
      return new FloatValue((float)Math.sqrt(this._value));
   }

   @Override
   public final Value Sub(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         float fValue = ((FloatValue)value)._value;
         this._value -= fValue;
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Add(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         float fValue = ((FloatValue)value)._value;
         this._value += fValue;
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Incr() {
      this._value += 1065353216;
      return this;
   }

   @Override
   public final Value Decr() {
      this._value -= 1065353216;
      return this;
   }

   @Override
   public final Value Div(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         float fValue = ((FloatValue)value)._value;
         this._value /= fValue;
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Eq(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         return this._value == ((FloatValue)val)._value ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return BooleanValue.FALSE;
      }
   }

   @Override
   public final Value GE(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         return this._value >= ((FloatValue)val)._value ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return BooleanValue.FALSE;
      }
   }

   @Override
   public final Value GT(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         return this._value > ((FloatValue)val)._value ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return BooleanValue.FALSE;
      }
   }

   @Override
   public final Value LE(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         return this._value <= ((FloatValue)val)._value ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return BooleanValue.FALSE;
      }
   }

   @Override
   public final Value LT(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         return this._value < ((FloatValue)val)._value ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return BooleanValue.FALSE;
      }
   }

   @Override
   public final Value Mul(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         float f = ((FloatValue)val)._value;
         this._value *= f;
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value NE(Value value) {
      Value val = value.toFloatValue();
      if (val.isFloat()) {
         return this._value != ((FloatValue)val)._value ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return BooleanValue.FALSE;
      }
   }

   @Override
   public final Value UMinus() {
      this._value = -this._value;
      return this;
   }

   @Override
   public final Value abs() {
      return new FloatValue(Math.abs(this._value));
   }

   @Override
   public final Value clone() {
      return new FloatValue(this._value);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof FloatValue ? this._value == ((FloatValue)obj)._value : false;
   }

   @Override
   public final Value max(Value value) {
      Value val = value.toFloatValue();
      return val.isFloat() && this._value >= ((FloatValue)value)._value ? this.clone() : value.clone();
   }

   @Override
   public final Value min(Value value) {
      Value val = value.toFloatValue();
      return val.isFloat() && this._value <= ((FloatValue)value)._value ? this.clone() : value.clone();
   }

   @Override
   public final Value toBooleanValue() {
      return this._value == false ? BooleanValue.FALSE : BooleanValue.TRUE;
   }

   @Override
   public final String toString() {
      return ((StringValue)this.toStringValue()).getString();
   }

   @Override
   public final Value toStringValue() {
      return new StringValue(Float.toString(this._value));
   }

   @Override
   public final Value parseFloat() {
      return this.clone();
   }
}
