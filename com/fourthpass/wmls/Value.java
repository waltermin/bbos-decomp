package com.fourthpass.wmls;

public class Value {
   public static final Value INVALID = new Value();
   public static final int INTEGER_TYPE;
   public static final int FLOAT_TYPE;
   public static final int STRING_TYPE;
   public static final int BOOLEAN_TYPE;
   public static final int INVALID_TYPE;

   public Value Add(Value value) {
      return INVALID;
   }

   public Value Div(Value value) {
      return INVALID;
   }

   public Value IDiv(Value value) {
      return INVALID;
   }

   public Value Rem(Value value) {
      return INVALID;
   }

   public Value Sub(Value value) {
      return INVALID;
   }

   public Value UMinus() {
      return INVALID;
   }

   public Value Mul(Value value) {
      return INVALID;
   }

   public Value BAnd(Value value) {
      return INVALID;
   }

   public Value BNot() {
      return INVALID;
   }

   public Value BOr(Value value) {
      return INVALID;
   }

   public Value BLShift(Value value) {
      return INVALID;
   }

   public Value BRShift(Value value) {
      return INVALID;
   }

   public Value BRSZShift(Value value) {
      return INVALID;
   }

   public Value BXOr(Value value) {
      return INVALID;
   }

   public Value Decr() {
      return INVALID;
   }

   public Value Incr() {
      return INVALID;
   }

   public Value Eq(Value value) {
      return INVALID;
   }

   public Value GE(Value value) {
      return INVALID;
   }

   public Value GT(Value value) {
      return INVALID;
   }

   public Value LE(Value value) {
      return INVALID;
   }

   public Value LT(Value value) {
      return INVALID;
   }

   public Value NE(Value value) {
      return INVALID;
   }

   public Value Not() {
      return INVALID;
   }

   public boolean isFloat() {
      return this.typeOf() == 1;
   }

   public boolean isInteger() {
      return this.typeOf() == 0;
   }

   public boolean isInvalid() {
      return this.typeOf() == 4;
   }

   public boolean isString() {
      return this.typeOf() == 2;
   }

   public Value _int() {
      return INVALID;
   }

   public Value abs() {
      return INVALID;
   }

   public Value max(Value value) {
      return INVALID;
   }

   public Value min(Value value) {
      return INVALID;
   }

   public Value parseInt() {
      return INVALID;
   }

   public Value parseFloat() {
      return INVALID;
   }

   public static Value parseLiteral(String s) {
      try {
         if (s.charAt(0) == '"') {
            return new StringValue(s.substring(1, s.lastIndexOf(34)));
         } else if (s.charAt(0) == '\'') {
            return new StringValue(s.substring(1, s.lastIndexOf(39)));
         } else if (s.equals("true")) {
            return BooleanValue.TRUE;
         } else {
            return s.equals("false") ? BooleanValue.FALSE : new IntegerValue(Integer.parseInt(s.toLowerCase()));
         }
      } finally {
         return new StringValue(s);
      }
   }

   public Value toBooleanValue() {
      return INVALID;
   }

   public Value toFloatValue() {
      return INVALID;
   }

   public Value toIntegerValue() {
      return INVALID;
   }

   public Value toStringValue() {
      return INVALID;
   }

   public int typeOf() {
      return 4;
   }

   public Value clone() {
      return this;
   }

   @Override
   public String toString() {
      return "invalid";
   }
}
