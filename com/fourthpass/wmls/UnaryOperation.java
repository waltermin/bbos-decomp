package com.fourthpass.wmls;

final class UnaryOperation {
   private Value _UOp;

   UnaryOperation(Value value) {
      this.convert(value);
   }

   UnaryOperation() {
      this(Value.INVALID);
   }

   final void convert(Value value) {
      this._UOp = value.toIntegerValue();
      if (this._UOp.isInvalid()) {
         this._UOp = value.toFloatValue();
      }
   }

   public static final Value getConversion(Value value) {
      Value result = value.toIntegerValue();
      if (result.isInvalid()) {
         result = value.toFloatValue();
      }

      return result;
   }

   public final Value _int() {
      return this._UOp._int();
   }

   public final Value abs() {
      return this._UOp.abs();
   }
}
