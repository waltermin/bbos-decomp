package com.fourthpass.wmls;

public final class StringValue extends Value {
   private String _value;
   private static final int MAX_STRING_LENGTH = 32768;
   static final StringValue EMPTY_STRING = new StringValue("");

   public StringValue(String s) {
      this._value = s;
   }

   @Override
   public final Value Add(Value value) {
      Value stringVal = value.toStringValue();
      if (stringVal.isString()) {
         String otherValue = ((StringValue)stringVal).getValue();
         if (this._value.length() + otherValue.length() > 32768) {
            throw new RuntimeException("WMLScript string too large");
         }

         this._value = this._value + otherValue;
         return this;
      } else {
         return Value.INVALID;
      }
   }

   @Override
   public final Value Eq(Value value) {
      Value stringVal = value.toStringValue();
      return stringVal.isString() && this._value.compareTo(((StringValue)stringVal).getValue()) == 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value GE(Value value) {
      Value stringVal = value.toStringValue();
      return stringVal.isString() && this._value.compareTo(((StringValue)stringVal).getValue()) >= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value GT(Value value) {
      return value.toStringValue().isString() && this._value.compareTo(((StringValue)value).getValue()) > 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value LE(Value value) {
      return value.toStringValue().isString() && this._value.compareTo(((StringValue)value).getValue()) <= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value LT(Value value) {
      return value.toStringValue().isString() && this._value.compareTo(((StringValue)value).getValue()) < 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value NE(Value value) {
      return value.toStringValue().isString() && this._value.compareTo(((StringValue)value).getValue()) != 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value clone() {
      return new StringValue(this._value);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof StringValue ? this._value.compareTo(((StringValue)obj).getValue()) == 0 : false;
   }

   public final String getString() {
      return this._value;
   }

   @Override
   public final Value parseFloat() {
      String s = this._value.trim();
      if (s.length() == 0) {
         return Value.INVALID;
      }

      if (s.charAt(0) == '#') {
         return Value.INVALID;
      }

      if (s.charAt(0) == '+') {
         s = s.substring(1);
      }

      int i = s.indexOf(32);
      if (i != -1) {
         s = s.substring(0, i);
         if (s.length() == 0) {
            return Value.INVALID;
         }
      }

      try {
         float f = Float.parseFloat(s);
         return new FloatValue(f);
      } finally {
         ;
      }
   }

   @Override
   public final Value parseInt() {
      String s = this._value.trim();
      if (s.length() == 0) {
         return Value.INVALID;
      }

      if (s.charAt(0) == '#') {
         return Value.INVALID;
      }

      if (s.charAt(0) == '+') {
         s = s.substring(1);
      }

      int i = s.indexOf(32);
      if (i != -1) {
         s = s.substring(0, i);
         if (s.length() == 0) {
            return Value.INVALID;
         }
      }

      StringBuffer _valuebuffer = new StringBuffer(s.length());
      new Integer(0);

      for (int j = 0; j < s.length(); j++) {
         char c = s.charAt(j);
         if (c == '-' && j == 0) {
            _valuebuffer.append(c);
         } else {
            if (c < '0' || c > '9') {
               break;
            }

            _valuebuffer.append(c);
         }
      }

      if (_valuebuffer.length() == 0) {
         return Value.INVALID;
      }

      Integer integer;
      try {
         integer = Integer.valueOf(_valuebuffer.toString());
      } finally {
         ;
      }

      return integer <= Integer.MAX_VALUE && integer >= Integer.MIN_VALUE ? new IntegerValue(integer) : Value.INVALID;
   }

   @Override
   public final Value toBooleanValue() {
      return this._value.length() != 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   @Override
   public final Value toFloatValue() {
      Value value = this.parseFloat();
      return value.isFloat() ? value : Value.INVALID;
   }

   @Override
   public final Value toIntegerValue() {
      Value value = this.parseInt();
      return value.isInteger() ? value : Value.INVALID;
   }

   @Override
   public final String toString() {
      return this._value;
   }

   public final String getValue() {
      return this._value;
   }

   @Override
   public final Value toStringValue() {
      return this;
   }

   @Override
   public final int typeOf() {
      return 2;
   }
}
