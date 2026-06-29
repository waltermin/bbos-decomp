package com.fourthpass.wmls;

class MathOperation extends BinaryOperation {
   MathOperation() {
   }

   MathOperation(Value value, Value value1) {
      this.convert(value, value1);
   }

   public Value Div() {
      return super._LOp.Div(super._ROp).toFloatValue();
   }

   public Value Mul() {
      return super._LOp.Mul(super._ROp);
   }

   public Value Sub() {
      return super._LOp.Sub(super._ROp);
   }

   @Override
   void convert(Value lhs, Value rhs) {
      if (!lhs.isFloat() && !rhs.isFloat()) {
         super._LOp = lhs.toIntegerValue();
         if (!super._LOp.isInvalid()) {
            super._ROp = rhs.toIntegerValue();
            if (!super._ROp.isInvalid()) {
               return;
            }
         }

         super._ROp = rhs.toFloatValue();
         if (super._ROp.isInvalid()) {
            super._LOp = Value.INVALID;
         } else {
            super._LOp = lhs.toFloatValue();
         }
      } else {
         super._ROp = rhs.toFloatValue();
         if (super._ROp.isInvalid()) {
            super._LOp = Value.INVALID;
         } else {
            super._LOp = lhs.toFloatValue();
         }
      }
   }
}
