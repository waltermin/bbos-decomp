package com.fourthpass.wmls;

final class ConstantString extends Constant {
   private StringValue _stringValue;

   public ConstantString(String string) {
      this._stringValue = new StringValue(string);
   }

   public ConstantString() {
      this._stringValue = StringValue.EMPTY_STRING;
   }

   @Override
   final Value getValue() {
      return this._stringValue.clone();
   }

   @Override
   public final String toString() {
      return this._stringValue.toString();
   }
}
