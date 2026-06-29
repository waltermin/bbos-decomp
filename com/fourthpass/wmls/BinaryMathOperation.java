package com.fourthpass.wmls;

final class BinaryMathOperation extends MathOperation {
   Value _lv;
   Value _rv;

   public BinaryMathOperation() {
   }

   public BinaryMathOperation(Value lhs, Value rhs) {
      this.convert(lhs, rhs);
   }

   @Override
   final void convert(Value lhs, Value rhs) {
      super.convert(lhs, rhs);
      this._lv = lhs;
      this._rv = rhs;
   }

   public final Value max() {
      return ((BooleanValue)super._LOp.max(super._ROp).Eq(super._LOp)).getValue() ? this._lv.clone() : this._rv.clone();
   }

   public final Value min() {
      return ((BooleanValue)super._LOp.min(super._ROp).Eq(super._LOp)).getValue() ? this._lv.clone() : this._rv.clone();
   }
}
