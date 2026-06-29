package com.fourthpass.wmls;

final class ConstantFloat extends Constant {
   ConstantFloat(float value) {
      super._value = new FloatValue(value);
   }

   @Override
   final Value getValue() {
      return super._value.clone();
   }
}
