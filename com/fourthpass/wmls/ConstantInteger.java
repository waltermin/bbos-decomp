package com.fourthpass.wmls;

final class ConstantInteger extends Constant {
   ConstantInteger(int int32) {
      super._value = new IntegerValue(int32);
   }

   @Override
   final Value getValue() {
      return super._value.clone();
   }
}
