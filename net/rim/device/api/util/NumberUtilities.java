package net.rim.device.api.util;

public final class NumberUtilities {
   private NumberUtilities() {
   }

   private static final int cast2Int(byte num, int radix) {
      switch (radix) {
         case 2:
         case 4:
         case 8:
         case 16:
            return num & 0xFF;
         default:
            return num;
      }
   }

   private static final int cast2Int(short num, int radix) {
      switch (radix) {
         case 2:
         case 4:
         case 8:
         case 16:
            return num & 65535;
         default:
            return num;
      }
   }

   public static final void appendNumber(StringBuffer strBuf, byte num, int radix) {
      appendNumber(strBuf, num, radix, -1);
   }

   public static final void appendNumber(StringBuffer strBuf, byte num, int radix, int minWidth) {
      if (radix == 10 && minWidth <= 0) {
         strBuf.append(num);
      } else {
         appendNumber(strBuf, cast2Int(num, radix), radix, minWidth);
      }
   }

   public static final String toString(byte num, int radix) {
      return toString(num, radix, -1);
   }

   public static final String toString(byte num, int radix, int minWidth) {
      return toString(cast2Int(num, radix), radix, minWidth);
   }

   public static final void appendNumber(StringBuffer strBuf, short num, int radix) {
      appendNumber(strBuf, num, radix, -1);
   }

   public static final void appendNumber(StringBuffer strBuf, short num, int radix, int minWidth) {
      appendNumber(strBuf, cast2Int(num, radix), radix, minWidth);
   }

   public static final String toString(short num, int radix) {
      return toString(num, radix, -1);
   }

   public static final String toString(short num, int radix, int minWidth) {
      return toString(cast2Int(num, radix), radix, minWidth);
   }

   public static final void appendNumber(StringBuffer strBuf, int num) {
      appendNumber(strBuf, num, 10, -1);
   }

   public static final void appendNumber(StringBuffer strBuf, int num, int radix) {
      appendNumber(strBuf, num, radix, -1);
   }

   public static final native void appendNumber(StringBuffer var0, int var1, int var2, int var3);

   public static final int appendNumber(int offset, byte[] byteBuf, int num, int radix) {
      return appendNumber(byteBuf, offset, num, radix);
   }

   public static final int appendNumber(int offset, byte[] byteBuf, long num, int radix) {
      return appendNumber(byteBuf, offset, num, radix);
   }

   private static final native int appendNumber(byte[] var0, int var1, long var2, int var4);

   public static final String toString(int num, int radix) {
      return toString(num, radix, -1);
   }

   public static final String toString(int num, int radix, int minWidth) {
      StringBuffer strBuf = new StringBuffer(Math.max(10, minWidth));
      appendNumber(strBuf, num, radix, minWidth);
      return strBuf.toString();
   }

   public static final void appendNumber(StringBuffer strBuf, long num) {
      appendNumber(strBuf, num, 10, -1);
   }

   public static final void appendNumber(StringBuffer strBuf, long num, int radix) {
      appendNumber(strBuf, num, radix, -1);
   }

   public static final native void appendNumber(StringBuffer var0, long var1, int var3, int var4);

   public static final String toString(long num, int radix) {
      return toString(num, radix, -1);
   }

   public static final String toString(long num, int radix, int minWidth) {
      StringBuffer strBuf = new StringBuffer(Math.max(20, minWidth));
      appendNumber(strBuf, num, radix, minWidth);
      return strBuf.toString();
   }

   public static final char intToHexDigit(int value) {
      value &= 15;
      return value < 10 ? (char)(value + 48) : (char)(value - 10 + 97);
   }

   public static final char intToUpperHexDigit(int value) {
      value &= 15;
      return value < 10 ? (char)(value + 48) : (char)(value - 10 + 65);
   }

   public static final int hexDigitToInt(char dig) {
      switch (dig) {
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
            return dig - 48;
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
            return dig - 65 + 10;
         case 'a':
         case 'b':
         case 'c':
         case 'd':
         case 'e':
         case 'f':
            return dig - 97 + 10;
         default:
            throw new NumberFormatException();
      }
   }

   public static final int hexDigitToInt(char dig, int err_val) {
      switch (dig) {
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
            return dig - 48;
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
            return dig - 65 + 10;
         case 'a':
         case 'b':
         case 'c':
         case 'd':
         case 'e':
         case 'f':
            return dig - 97 + 10;
         default:
            return err_val;
      }
   }

   public static final int parseInt(String s, int fromIndex, int toIndex, int radix) {
      if (s == null) {
         throw new NumberFormatException("null");
      }

      if (fromIndex >= 0 && toIndex >= fromIndex) {
         if (radix >= 2 && radix <= 36) {
            int result = 0;
            boolean negative = false;
            int i = fromIndex;
            int max = Math.min(s.length(), toIndex);
            if (max <= fromIndex) {
               throw new NumberFormatException(s);
            }

            int limit;
            if (s.charAt(i) == '-') {
               negative = true;
               limit = Integer.MIN_VALUE;
               i++;
            } else {
               limit = -2147483647;
            }

            int multmin = limit / radix;
            if (i < max) {
               int digit = Character.digit(s.charAt(i++), radix);
               if (digit < 0) {
                  throw new NumberFormatException(s);
               }

               result = -digit;
            }

            while (i < max) {
               int digit = Character.digit(s.charAt(i++), radix);
               if (digit < 0) {
                  throw new NumberFormatException(s);
               }

               if (result < multmin) {
                  throw new NumberFormatException(s);
               }

               result *= radix;
               if (result < limit + digit) {
                  throw new NumberFormatException(s);
               }

               result -= digit;
            }

            if (!negative) {
               return -result;
            } else if (i > 1) {
               return result;
            } else {
               throw new NumberFormatException(s);
            }
         } else {
            StringBuffer msg = new StringBuffer("radix ").append(radix);
            if (radix < 2) {
               msg.append(" less than Character.MIN_RADIX");
            } else {
               msg.append(" greater than Character.MAX_RADIX");
            }

            throw new NumberFormatException(msg.toString());
         }
      } else {
         throw new NumberFormatException("range");
      }
   }
}
