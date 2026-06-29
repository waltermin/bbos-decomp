package com.fourthpass.wmls;

import java.util.Random;

final class L0 extends Lib {
   private static Random _randomizer = new Random();
   public static final short ABS = 0;
   public static final short MIN = 1;
   public static final short MAX = 2;
   public static final short PARSE_INT = 3;
   public static final short PARSE_FLOAT = 4;
   public static final short IS_INT = 5;
   public static final short IS_FLOAT = 6;
   public static final short MAX_INT = 7;
   public static final short MIN_INT = 8;
   public static final short FLOAT = 9;
   public static final short EXIT = 10;
   public static final short HALT = 11;
   public static final short RANDOM = 12;
   public static final short SEED = 13;
   public static final short CHARACTERSET = 14;

   @Override
   public final Value invoke(int func, Interpreter$Engine engine) throws Exception {
      switch (func) {
         case -1:
            throw new Exception("Invalid Function Id");
         case 0:
         default:
            return abs(engine.popStack());
         case 1:
            return min(engine.popStack(), engine.popStack());
         case 2:
            return max(engine.popStack(), engine.popStack());
         case 3:
            return parseInt(engine.popStack());
         case 4:
            return parseFloat(engine.popStack());
         case 5:
            return isInt(engine.popStack());
         case 6:
            return isFloat(engine.popStack());
         case 7:
            return maxInt();
         case 8:
            return minInt();
         case 9:
            return BooleanValue.TRUE;
         case 10:
            Value status = engine.popStack();
            engine.halt();
            return status;
         case 11:
            Value stat = engine.popStack();
            engine.halt();
            if (stat.isInvalid()) {
               return new StringValue(stat.toString());
            }

            return stat;
         case 12:
            return random(engine.popStack());
         case 13:
            return seed(engine.popStack());
         case 14:
            return characterSet();
      }
   }

   public static final Value abs(Value value) {
      value = convertToNumber(value);
      return value.isInvalid() ? Value.INVALID : new UnaryOperation(value).abs();
   }

   public static final Value min(Value value1, Value value2) {
      value2 = convertToNumber(value2);
      value1 = convertToNumber(value1);
      return !value2.isInvalid() && !value1.isInvalid() ? new BinaryMathOperation(value2, value1).min() : Value.INVALID;
   }

   public static final Value max(Value value1, Value value2) {
      value2 = convertToNumber(value2);
      value1 = convertToNumber(value1);
      return !value2.isInvalid() && !value1.isInvalid() ? new BinaryMathOperation(value1, value2).max() : Value.INVALID;
   }

   public static final Value parseInt(Value value) {
      return value.isInvalid() ? Value.INVALID : value.parseInt();
   }

   public static final Value parseFloat(Value value) {
      return value.isInvalid() ? Value.INVALID : value.parseFloat();
   }

   static final Value isInt(Value value) {
      if (value.isInvalid()) {
         return Value.INVALID;
      } else {
         Value val = value.toIntegerValue();
         if (val.isInvalid()) {
            return BooleanValue.FALSE;
         } else {
            return val.isInteger() ? BooleanValue.TRUE : BooleanValue.FALSE;
         }
      }
   }

   static final Value isFloat(Value value) {
      if (value.isInvalid()) {
         return Value.INVALID;
      } else {
         Value val = value.toFloatValue();
         if (val.isInvalid()) {
            return BooleanValue.FALSE;
         } else {
            return val.isFloat() ? BooleanValue.TRUE : BooleanValue.FALSE;
         }
      }
   }

   public static final Value maxInt() {
      return new IntegerValue(Integer.MAX_VALUE);
   }

   public static final Value minInt() {
      return new IntegerValue(Integer.MIN_VALUE);
   }

   public static final Value random(Value value) {
      Value random = new UnaryOperation(value)._int();
      int i = -1;
      if (random.isFloat()) {
         random = ((FloatValue)random).toInt();
      }

      if (random.isInteger()) {
         i = ((IntegerValue)random).getValue();
      }

      if (i < 0) {
         return Value.INVALID;
      } else {
         return i == 0 ? new IntegerValue(0) : new IntegerValue(Math.abs(_randomizer.nextInt() % i));
      }
   }

   public static final Value seed(Value value) {
      Value val = new UnaryOperation(value)._int();
      long l = -1;
      if (val.isInteger()) {
         l = ((IntegerValue)val).getValue();
         if (l < 0) {
            l = System.currentTimeMillis();
         }

         _randomizer.setSeed(l);
         return StringValue.EMPTY_STRING.clone();
      } else {
         return Value.INVALID;
      }
   }

   public static final Value characterSet() {
      return new IntegerValue(106);
   }

   private static final Value convertToNumber(Value value) {
      switch (value.typeOf()) {
         case 1:
            return value;
         case 2:
            Value ivalue = Value.parseLiteral(value.toString());
            if (ivalue.isString()) {
               return Value.INVALID;
            }

            return ivalue;
         case 3:
         default:
            if (!((BooleanValue)value).getValue()) {
               return new IntegerValue(0);
            }

            return new IntegerValue(1);
         case 4:
            return Value.INVALID;
      }
   }
}
