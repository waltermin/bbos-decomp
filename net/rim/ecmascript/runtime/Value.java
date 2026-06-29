package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

public class Value {
   public static final int TypeInteger;
   static final long TypeIntegerValueMask;
   static final long TypeIntegerTypeMask;
   public static final int TypeReturnAddress;
   public static final int TypeUndefined;
   public static final int TypeNull;
   public static final int TypeBoolean;
   public static final int TypeString;
   public static final int TypeObject;
   public static final int TypeDouble;
   static final long DENORMAL_DISGUISED_AS_NAN;
   public static final long UNDEFINED = makeValue(2, 0);
   static final long PLACEHOLDER_GLOBAL = makeValue(2, 1);
   static final long PLACEHOLDER_ARRAY = makeValue(2, 2);
   public static final long DEFAULT = makeValue(2, 3);
   public static final long NULL = makeValue(3, 0);
   public static final long TRUE = makeBooleanValue(true);
   public static final long FALSE = makeBooleanValue(false);
   public static final long NaN = Double.doubleToLongBits((double)9221120237041090560L);
   public static final long POSITIVE_INFINITY = makeDoubleValue((double)9218868437227405312L);
   public static final long NEGATIVE_INFINITY = makeDoubleValue((double)-4503599627370496L);
   public static final long ONE = makeIntegerValue(1);
   public static final long MINUS_ONE = makeIntegerValue(-1);
   public static final long ZERO = makeIntegerValue(0);
   public static final long PLUS_ZERO = makeDoubleValue((double)0L);
   public static final long MINUS_ZERO = makeDoubleValue((double)Long.MIN_VALUE);
   public static final int MaxStringLength;

   private static long makeValue(int type, int value) {
      return ((long)type << 32) + (value & 4294967295L);
   }

   protected Value() {
   }

   public static boolean isPrimitive(long value) {
      switch (getType(value)) {
         case -1:
            return false;
         case 0:
         case 1:
         case 3:
         case 4:
         case 5:
         case 7:
            return true;
         case 2:
         default:
            if (value == UNDEFINED) {
               return true;
            }

            return false;
         case 6:
            return false;
      }
   }

   public static native int getType(long var0);

   public static long makeReturnAddressValue(int i) {
      return makeValue(1, i);
   }

   public static native long makeIntegerValue(int var0);

   public static native long makeBooleanValue(boolean var0);

   public static native long makeStringValue(String var0);

   public static native long makeObjectValue(ESObject var0);

   public static long makeLongValue(long l) {
      return (int)l == l ? l : makeDoubleValue(l);
   }

   public static long makeDoubleValue(double d) {
      if (d != d) {
         return NaN;
      }

      int i = (int)d;
      if (Misc.compareDouble(i, d) == 0) {
         return i & 4294967295L;
      }

      long l = Double.doubleToLongBits(d);
      if (getType(l) != 7) {
         l |= -68719476736L;
      }

      return l;
   }

   public static int getIntegerValue(long value) {
      return (int)value;
   }

   public static int getReturnAddressValue(long value) {
      return (int)value;
   }

   public static boolean getBooleanValue(long value) {
      return (int)value != 0;
   }

   public static native String getStringValue(long var0);

   public static native ESObject getObjectValue(long var0);

   public static native ESObject checkIfObjectValue(long var0);

   public static native ESFunction checkIfFunctionValue(long var0);

   static JavaField checkIfJavaFieldValue(long value) {
      if (getType(value) != 6) {
         return null;
      }

      Object o = Misc.toObject((int)value);

      try {
         return (JavaField)o;
      } finally {
         ;
      }
   }

   public static ESArray checkIfArrayValue(long value) {
      if (getType(value) != 6) {
         return null;
      }

      try {
         return (ESArray)Misc.toObject((int)value);
      } finally {
         ;
      }
   }

   public static double getDoubleValue(long value) {
      if ((value & -68719476736L) == -68719476736L) {
         value &= 68719476735L;
      }

      return Double.longBitsToDouble(value);
   }

   static long typeOf(long value) {
      String str;
      switch (getType(value)) {
         case -1:
         case 1:
            str = "Error";
            break;
         case 0:
         case 7:
            str = "number";
            break;
         case 2:
         default:
            str = "undefined";
            break;
         case 3:
            str = "object";
            break;
         case 4:
            str = "boolean";
            break;
         case 5:
            str = "string";
            break;
         case 6:
            if (checkIfFunctionValue(value) != null) {
               str = "function";
            } else {
               str = "object";
            }
      }

      return makeStringValue(str);
   }
}
