package com.fourthpass.wmls;

final class BitOperation extends BinaryOperation {
   BitOperation() {
   }

   BitOperation(Value first, Value second) {
      this.convert(first, second);
   }

   final Value BAnd() {
      return super._LOp.BAnd(super._ROp);
   }

   final Value BLShift() {
      return super._LOp.BLShift(super._ROp);
   }

   final Value BOr() {
      return super._LOp.BOr(super._ROp);
   }

   final Value BRSShift() {
      return super._LOp.BRShift(super._ROp);
   }

   final Value BRSZShift() {
      return super._LOp.BRSZShift(super._ROp);
   }

   final Value BXOr() {
      return super._LOp.BXOr(super._ROp);
   }

   final Value IDiv() {
      return super._LOp.IDiv(super._ROp);
   }

   final Value Rem() {
      return super._LOp.Rem(super._ROp);
   }

   @Override
   final void convert(Value value, Value value1) {
      super._ROp = value1.toIntegerValue();
      if (super._ROp.isInvalid()) {
         super._LOp = Value.INVALID;
      } else {
         super._LOp = value.toIntegerValue();
      }
   }
}
