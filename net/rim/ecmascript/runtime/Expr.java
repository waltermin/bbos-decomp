package net.rim.ecmascript.runtime;

public class Expr extends Convert {
   private Expr() {
   }

   public static long add(long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         long ll = (int)x;
         long rl = (int)y;
         ll += rl;
         if (ll <= Integer.MAX_VALUE && ll >= Integer.MIN_VALUE) {
            return ll & 4294967295L;
         }
      }

      x = Convert.toPrimitive(x);
      y = Convert.toPrimitive(y);
      if (Value.getType(y) != 5 && Value.getType(x) != 5) {
         return Value.makeDoubleValue(Convert.toDouble(x) + Convert.toDouble(y));
      }

      String sx = Convert.toString(x);
      String sy = Convert.toString(y);
      StringBuffer buff = (StringBuffer)(new Object(sx.length() + sy.length()));
      buff.append(sx);
      buff.append(sy);
      return Value.makeStringValue(buff.toString());
   }

   public static long inc(long x) {
      return inc(x, 1);
   }

   public static long inc(long x, int by) {
      if ((x & -4294967296L) == 0) {
         long ix = (int)x + by;
         if (ix <= Integer.MAX_VALUE && ix >= Integer.MIN_VALUE) {
            return ix & 4294967295L;
         }
      }

      return Value.makeDoubleValue(Convert.toDouble(x) + by);
   }

   public static long bitand(long x, long y) {
      return ((x | y) & -4294967296L) == 0 ? x & y : Value.makeIntegerValue(Convert.toInt32(x) & Convert.toInt32(y));
   }

   public static long bitnot(long x) {
      return (x & -4294967296L) == 0 ? (x ^ -1) & 4294967295L : Value.makeIntegerValue(~Convert.toInt32(x));
   }

   public static long bitor(long x, long y) {
      return ((x | y) & -4294967296L) == 0 ? x | y : Value.makeIntegerValue(Convert.toInt32(x) | Convert.toInt32(y));
   }

   public static long bitxor(long x, long y) {
      return ((x | y) & -4294967296L) == 0 ? x ^ y : Value.makeIntegerValue(Convert.toInt32(x) ^ Convert.toInt32(y));
   }

   public static boolean le(long x, long y) {
      return le(false, x, y);
   }

   static boolean le(boolean reverse, long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         return (int)x <= (int)y;
      } else {
         x = Convert.toNumberPrimitive(x);
         y = Convert.toNumberPrimitive(y);
         if (Value.getType(x) != 5 || Value.getType(y) != 5) {
            double dx = Convert.toDouble(x);
            double dy = Convert.toDouble(y);
            return dx != dx || dy != dy ? reverse : dx <= dy;
         } else {
            return Value.getStringValue(x).compareTo(Value.getStringValue(y)) <= 0;
         }
      }
   }

   public static boolean lt(long x, long y) {
      return lt(false, x, y);
   }

   static boolean lt(boolean reverse, long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         return (int)x < (int)y;
      } else {
         x = Convert.toNumberPrimitive(x);
         y = Convert.toNumberPrimitive(y);
         if (Value.getType(x) != 5 || Value.getType(y) != 5) {
            double dx = Convert.toDouble(x);
            double dy = Convert.toDouble(y);
            return dx != dx || dy != dy ? reverse : dx < dy;
         } else {
            return Value.getStringValue(x).compareTo(Value.getStringValue(y)) < 0;
         }
      }
   }

   public static boolean ge(long x, long y) {
      return ge(false, x, y);
   }

   static boolean ge(boolean reverse, long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         return (int)x >= (int)y;
      } else {
         x = Convert.toNumberPrimitive(x);
         y = Convert.toNumberPrimitive(y);
         if (Value.getType(x) != 5 || Value.getType(y) != 5) {
            double dx = Convert.toDouble(x);
            double dy = Convert.toDouble(y);
            return dx != dx || dy != dy ? reverse : dx >= dy;
         } else {
            return Value.getStringValue(x).compareTo(Value.getStringValue(y)) >= 0;
         }
      }
   }

   public static boolean gt(long x, long y) {
      return gt(false, x, y);
   }

   static boolean gt(boolean reverse, long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         return (int)x > (int)y;
      } else {
         x = Convert.toNumberPrimitive(x);
         y = Convert.toNumberPrimitive(y);
         if (Value.getType(x) != 5 || Value.getType(y) != 5) {
            double dx = Convert.toDouble(x);
            double dy = Convert.toDouble(y);
            return dx != dx || dy != dy ? reverse : dx > dy;
         } else {
            return Value.getStringValue(x).compareTo(Value.getStringValue(y)) > 0;
         }
      }
   }

   public static long div(long x, long y) {
      return Value.makeDoubleValue(Convert.toDouble(x) / Convert.toDouble(y));
   }

   public static long lsh(long x, long y) {
      return ((x | y) & -4294967296L) == 0 ? (int)x << (int)y & 4294967295L : Value.makeDoubleValue(Convert.toInt32(x) << Convert.toInt32(y));
   }

   public static long rsh(long x, long y) {
      return ((x | y) & -4294967296L) == 0 ? (int)x >> (int)y & 4294967295L : Value.makeDoubleValue(Convert.toInt32(x) >> Convert.toInt32(y));
   }

   public static long ursh(long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         int ix = (int)x;
         if (ix >= 0) {
            return ix >>> (int)y & 4294967295L;
         }
      }

      return Value.makeDoubleValue(Convert.toInt32(x) >>> Convert.toInt32(y) & 4294967295L);
   }

   public static long mod(long x, long y) {
      return Value.makeDoubleValue(Convert.toDouble(x) % Convert.toDouble(y));
   }

   public static long mul(long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         long ll = (int)x;
         long rl = (int)y;
         ll *= rl;
         if (ll <= Integer.MAX_VALUE && ll >= Integer.MIN_VALUE) {
            return ll & 4294967295L;
         }
      }

      return Value.makeDoubleValue(Convert.toDouble(x) * Convert.toDouble(y));
   }

   public static long sub(long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         long ll = (int)x;
         long rl = (int)y;
         ll -= rl;
         if (ll <= Integer.MAX_VALUE && ll >= Integer.MIN_VALUE) {
            return ll & 4294967295L;
         }
      }

      return Value.makeDoubleValue(Convert.toDouble(x) - Convert.toDouble(y));
   }

   public static long neg(long x) {
      if ((x & -4294967296L) == 0) {
         int value = (int)x;
         if (value == Integer.MIN_VALUE) {
            return Value.makeDoubleValue(-Convert.toDouble(x));
         } else {
            return value == 0 ? Value.makeDoubleValue((double)Long.MIN_VALUE) : -value & 4294967295L;
         }
      } else {
         return Value.makeDoubleValue(-Convert.toDouble(x));
      }
   }

   public static boolean eq(long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         return x == y;
      }

      int typeY = Value.getType(y);
      switch (Value.getType(x)) {
         case -1:
         case 1:
            return false;
         case 0:
         default:
            switch (typeY) {
               case -1:
               case 1:
                  return false;
               case 0:
               default:
                  if (x == y) {
                     return true;
                  }

                  return false;
               case 2:
               case 3:
                  return false;
               case 4:
                  return eq(x, Convert.toNumber(y));
               case 5:
                  return eq(x, Convert.toNumber(y));
               case 6:
                  return eq(x, Convert.toPrimitive(y));
               case 7:
                  double dx = Value.getIntegerValue(x);
                  double dy = Value.getDoubleValue(y);
                  if (dx == dy) {
                     return true;
                  }

                  return false;
            }
         case 2:
            switch (typeY) {
               case -1:
               case 1:
                  return false;
               case 0:
               default:
                  return false;
               case 2:
                  return true;
               case 3:
                  return true;
               case 4:
                  return eq(x, Convert.toNumber(y));
               case 5:
               case 6:
               case 7:
                  return false;
            }
         case 3:
            switch (typeY) {
               case -1:
               case 1:
                  return false;
               case 0:
               default:
                  return false;
               case 2:
                  return true;
               case 3:
                  return true;
               case 4:
                  return eq(x, Convert.toNumber(y));
               case 5:
               case 6:
               case 7:
                  return false;
            }
         case 4:
            switch (typeY) {
               case 4:
                  if (x == y) {
                     return true;
                  }

                  return false;
               default:
                  return eq(Convert.toNumber(x), y);
            }
         case 5:
            switch (typeY) {
               case -1:
               case 1:
                  return false;
               case 0:
               default:
                  return eq(Convert.toNumber(x), y);
               case 2:
               case 3:
                  return false;
               case 4:
                  return eq(x, Convert.toNumber(y));
               case 5:
                  return Value.getStringValue(x).equals(Value.getStringValue(y));
               case 6:
                  return eq(x, Convert.toPrimitive(y));
               case 7:
                  return eq(Convert.toNumber(x), y);
            }
         case 6:
            switch (typeY) {
               case -1:
               case 1:
                  return false;
               case 0:
               default:
                  return eq(Convert.toPrimitive(x), y);
               case 2:
               case 3:
                  return false;
               case 4:
                  return eq(x, Convert.toNumber(y));
               case 5:
                  return eq(Convert.toPrimitive(x), y);
               case 6:
                  if (x == y) {
                     return true;
                  }

                  return false;
               case 7:
                  return eq(Convert.toPrimitive(x), y);
            }
         case 7:
            switch (typeY) {
               case -1:
               case 1:
                  return false;
               case 0:
               default:
                  double var9 = Value.getDoubleValue(x);
                  double var11 = Value.getIntegerValue(y);
                  if (var9 == var11) {
                     return true;
                  }

                  return false;
               case 2:
               case 3:
                  return false;
               case 4:
                  return eq(x, Convert.toNumber(y));
               case 5:
                  return eq(x, Convert.toNumber(y));
               case 6:
                  return eq(x, Convert.toPrimitive(y));
               case 7:
                  double dx = Value.getDoubleValue(x);
                  double dy = Value.getDoubleValue(y);
                  return dx == dy;
            }
      }
   }

   public static boolean stricteq(long x, long y) {
      if (((x | y) & -4294967296L) == 0) {
         return x == y;
      }

      int typeY = Value.getType(y);
      switch (Value.getType(x)) {
         case -1:
         case 1:
            return false;
         case 0:
         default:
            switch (typeY) {
               case 0:
                  if (x == y) {
                     return true;
                  }

                  return false;
               case 7:
                  double dx = Value.getIntegerValue(x);
                  double dy = Value.getDoubleValue(y);
                  if (dx == dy) {
                     return true;
                  }

                  return false;
               default:
                  return false;
            }
         case 2:
            if (Value.getType(y) == 2) {
               return true;
            }

            return false;
         case 3:
         case 4:
         case 6:
            if (x == y) {
               return true;
            }

            return false;
         case 5:
            switch (typeY) {
               case 5:
                  return Value.getStringValue(x).equals(Value.getStringValue(y));
               default:
                  return false;
            }
         case 7:
            switch (typeY) {
               case 0:
                  double var9 = Value.getDoubleValue(x);
                  double var11 = Value.getIntegerValue(y);
                  if (var9 == var11) {
                     return true;
                  }

                  return false;
               case 7:
                  double dx = Value.getDoubleValue(x);
                  double dy = Value.getDoubleValue(y);
                  if (dx == dy) {
                     return true;
                  }

                  return false;
               default:
                  return false;
            }
      }
   }
}
