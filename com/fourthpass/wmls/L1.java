package com.fourthpass.wmls;

import net.rim.device.api.util.MathUtilities;

final class L1 extends Lib {
   private static final FloatValue MAX_FLOAT = new FloatValue((float)2139095039);
   private static final FloatValue MIN_FLOAT = new FloatValue((float)1);

   @Override
   public final Value invoke(int func, Interpreter$Engine engine) {
      switch (func) {
         case -1:
            return Value.INVALID;
         case 0:
         default:
            return this.toInt(engine.popStack());
         case 1:
            return this.floor(engine.popStack());
         case 2:
            return this.ceil(engine.popStack());
         case 3:
            return this.pow(engine.popStack(), engine.popStack());
         case 4:
            return this.round(engine.popStack());
         case 5:
            return this.sqrt(engine.popStack());
         case 6:
            return MAX_FLOAT;
         case 7:
            return MIN_FLOAT;
      }
   }

   private final Value toInt(Value v) {
      if (v.isFloat()) {
         return ((FloatValue)v).toInt();
      } else {
         return v.isInteger() ? v : Value.INVALID;
      }
   }

   private final Value floor(Value v) {
      Value f = v.toFloatValue();
      return !f.isFloat() ? Value.INVALID : ((FloatValue)f).floor();
   }

   private final Value ceil(Value v) {
      Value f = v.toFloatValue();
      return !f.isFloat() ? Value.INVALID : ((FloatValue)f).ceil();
   }

   private final Value round(Value v) {
      Value f = v.toFloatValue();
      return !f.isFloat() ? Value.INVALID : ((FloatValue)f).round();
   }

   private final Value sqrt(Value v) {
      Value f = v.toFloatValue();
      return !f.isFloat() ? Value.INVALID : ((FloatValue)f).sqrt();
   }

   private final Value pow(Value v2, Value v1) {
      Value f1 = v1.toFloatValue();
      if (!f1.isFloat()) {
         return Value.INVALID;
      }

      float a = ((FloatValue)f1).getValue();
      if (a < 0 && !v2.isInteger()) {
         return Value.INVALID;
      }

      Value f2 = v2.toFloatValue();
      if (!f2.isFloat()) {
         return Value.INVALID;
      }

      float b = ((FloatValue)f2).getValue();
      return a == false && b < 0 ? Value.INVALID : new FloatValue((float)MathUtilities.exp(b * MathUtilities.log(a)));
   }
}
