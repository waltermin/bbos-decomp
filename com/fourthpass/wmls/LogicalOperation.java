package com.fourthpass.wmls;

final class LogicalOperation extends BinaryOperation {
   LogicalOperation() {
   }

   LogicalOperation(Value lhs, Value rhs) {
      this.convert(lhs, rhs);
   }

   final Value Add() {
      return super._LOp.Add(super._ROp);
   }

   final Value Eq() {
      return super._LOp.Eq(super._ROp);
   }

   final Value GE() {
      return super._LOp.GE(super._ROp);
   }

   final Value GT() {
      return super._LOp.GT(super._ROp);
   }

   final Value LE() {
      return super._LOp.LE(super._ROp);
   }

   final Value LT() {
      return super._LOp.LT(super._ROp);
   }

   final Value NE() {
      return super._LOp.NE(super._ROp);
   }

   @Override
   final void convert(Value lhs, Value rhs) {
      if (lhs.isString() || rhs.isString()) {
         super._ROp = rhs.toStringValue();
         if (super._ROp.isInvalid()) {
            super._LOp = Value.INVALID;
         } else {
            super._LOp = lhs.toStringValue();
         }
      } else if (!lhs.isFloat() && !rhs.isFloat()) {
         super._ROp = rhs.toIntegerValue();
         if (super._ROp.isInvalid()) {
            super._LOp = Value.INVALID;
         } else {
            super._LOp = lhs.toIntegerValue();
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
