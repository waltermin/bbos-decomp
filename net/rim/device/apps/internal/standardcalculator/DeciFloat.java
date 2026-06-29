package net.rim.device.apps.internal.standardcalculator;

import net.rim.device.api.util.Persistable;

public final class DeciFloat implements Persistable {
   private int _man;
   private int _exp;
   private static final int MANTISSA_MAX;
   private static final int MANTISSA_MIN;
   private static final int NUM_DIGITS;
   private static final int DISPLAY_DIGITS;
   private static final int EXPONENT_ZERO;
   private static final int ERROR_MANTISSA;
   private static final int INVALID_MANTISSA;
   private static final int SPECIAL_EXPONENT;
   private static final int[] PowerOf10 = new int[]{
      1,
      10,
      100,
      1000,
      10000,
      100000,
      1000000,
      10000000,
      100000000,
      -804913147,
      1310977,
      17104928,
      10,
      -804650999,
      301,
      302,
      303,
      304,
      305,
      306,
      408,
      409,
      509,
      -805044219,
      1718183726,
      10,
      -804651007,
      51,
      -804913147,
      4456513,
      4849735,
      76,
      -804913142,
      5439553,
      4587588,
      4718663
   };

   DeciFloat() {
      this._man = 0;
      this._exp = -8;
   }

   DeciFloat(int mantissa, int exponent) {
      this.normalize64(mantissa, exponent);
   }

   DeciFloat(int value) {
      this.normalize64(value, 0);
   }

   DeciFloat(String str) {
      this.stringToFP(str, 0, str.length());
   }

   DeciFloat(StringBuffer buffer, int offset, int len) {
      this.stringToFP((String)(new Object(buffer)), offset, len);
   }

   final void copy(DeciFloat dec) {
      if (dec != null) {
         this._man = dec._man;
         this._exp = dec._exp;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof DeciFloat)) {
         return false;
      }

      DeciFloat dec = (DeciFloat)obj;
      return this._exp == dec._exp && this._man == dec._man;
   }

   private final void overflow() {
      this._man = -99999999;
      this._exp = 99999999;
   }

   private final boolean isOverflow() {
      return this._exp == 99999999 && this._man == -99999999;
   }

   final void invalid() {
      this._man = -1;
      this._exp = 99999999;
   }

   final boolean isInvalid() {
      return this._exp == 99999999 && this._man == -1;
   }

   final boolean isZero() {
      return this._exp == -8 && this._man == 0;
   }

   private final void normalize64(long mantissa, int exponent) {
      boolean neg = false;
      if (mantissa < 0) {
         mantissa = -mantissa;
         neg = true;
      }

      if (mantissa < 100000000) {
         if (mantissa == 0) {
            exponent = -8;
         } else {
            do {
               mantissa *= 10;
               exponent--;
            } while (mantissa < 100000000);
         }
      } else {
         while (mantissa > 9999999990L) {
            mantissa /= 10;
            exponent++;
         }

         if (mantissa > 999999999) {
            mantissa = (mantissa + 5) / 10;
            exponent++;
         }
      }

      this._man = (int)(neg ? -mantissa : mantissa);
      this._exp = exponent;
   }

   public final void sub(DeciFloat b) {
      if (b != null) {
         this.doAdd(-b._man, b._exp);
      }
   }

   public final void add(DeciFloat b) {
      if (b != null) {
         this.doAdd(b._man, b._exp);
      }
   }

   public final void doAdd(int bMantissa, int bExp) {
      int aMantissa = this._man;
      int aExp = this._exp;
      if (aExp < bExp) {
         aExp = bExp;
         bExp = this._exp;
         aMantissa = bMantissa;
         bMantissa = this._man;
      }

      if (aExp - bExp >= 9) {
         bMantissa = 0;
      } else {
         while (bExp < aExp) {
            if (++bExp == aExp) {
               if (bMantissa > 0) {
                  bMantissa += 5;
               } else {
                  bMantissa -= 5;
               }
            }

            bMantissa /= 10;
            if (bMantissa == 0) {
               break;
            }
         }
      }

      aMantissa += bMantissa;
      this.normalize64(aMantissa, aExp);
   }

   public final void negate() {
      this._man = -this._man;
   }

   public final void mul(DeciFloat b) {
      if (b != null) {
         this.normalize64((long)this._man * b._man, this._exp + b._exp);
      }
   }

   public final void div(DeciFloat b) {
      if (b != null) {
         if (b._man == 0) {
            this.overflow();
            return;
         }

         long mantissa = (long)this._man * 1000000000 / b._man;
         this.normalize64(mantissa, this._exp - 9 - b._exp);
      }
   }

   public final void sqrt() {
      if (this._man < 0) {
         this.invalid();
      } else {
         long target = (long)this._man * 100000000;
         int exponent = this._exp;
         if ((exponent & 1) != 0) {
            exponent--;
            target *= 10;
         }

         exponent = (exponent - 8) / 2;
         long test = 0;

         for (int a = 30; a >= 0; a--) {
            long half = test | 1 << a;
            if (half * half <= target) {
               test = half;
            }
         }

         this.normalize64(test, exponent);
      }
   }

   private final void stringToFP(String buffer, int offset, int len) {
      long mantissa = 0;
      int exponent = -8;
      if (len != 0 && buffer != null) {
         exponent--;
         long digitValue = 100000000;
         boolean fractPart = false;
         boolean expPart = false;
         boolean negExp = false;
         int i = offset;
         int inlineExp = 0;
         if (buffer.charAt(i) == '-') {
            i++;
            len--;
            digitValue = -digitValue;
         } else if (buffer.charAt(i) == ' ') {
            i++;
            len--;
         }

         while (--len >= 0) {
            char c = buffer.charAt(i++);
            if (c == '.' && !fractPart) {
               fractPart = true;
            } else if (c >= '0' && c <= '9') {
               if (expPart) {
                  inlineExp = inlineExp * 10 + (c - '0');
               } else {
                  mantissa += digitValue * (c - '0');
                  digitValue /= 10;
               }

               if (!fractPart) {
                  exponent++;
               }
            } else if (c == 'e' && !expPart) {
               expPart = true;
               fractPart = true;
            } else if (c == '-' && expPart && !negExp) {
               negExp = true;
            } else if (c != ' ') {
               this.normalize64(0, 0);
               return;
            }
         }

         if (expPart) {
            if (negExp) {
               inlineExp = -inlineExp;
            }

            exponent += inlineExp;
         }
      }

      this.normalize64(mantissa, exponent);
   }

   private static final int round(int mantissa, int digits) {
      if (digits < 9) {
         int pow10 = mantissa < 0 ? -PowerOf10[9 - digits] : PowerOf10[9 - digits];
         mantissa += pow10 / 2;
         mantissa -= mantissa % pow10;
      }

      return mantissa;
   }

   @Override
   public final String toString() {
      int exponent = this._exp + 9;
      int mantissa = this._man;
      if (exponent - 1 <= 99 && exponent - 1 >= -99 && !this.isOverflow() && !this.isInvalid()) {
         StringBuffer buffer = (StringBuffer)(new Object());
         if (exponent > 8 || exponent <= -7) {
            mantissa = round(mantissa, 8);
            if (mantissa > 999999999) {
               mantissa = 100000000;
               exponent++;
            }

            mantissaToString(buffer, mantissa, 1, 8);
            buffer.append(' ');
            buffer.append('e');
            buffer.append(exponent - 1);
         } else if (exponent <= 0) {
            mantissaToString(buffer, mantissa, exponent, exponent + 8);
         } else {
            mantissaToString(buffer, mantissa, exponent, 8);
         }

         return buffer.toString();
      } else {
         return CalculatorUI.ERROR_MSG;
      }
   }

   private static final void mantissaToString(StringBuffer buffer, int mantissa, int digitsBeforeDecimal, int digits) {
      if (mantissa < 0) {
         buffer.append('-');
         mantissa = -mantissa;
      } else {
         buffer.append(' ');
      }

      mantissa = round(mantissa, digits);
      if (mantissa > 999999999) {
         mantissa = 100000000;
         digitsBeforeDecimal++;
      }

      if (digitsBeforeDecimal <= 0) {
         buffer.append('0');
         buffer.append('.');

         while (digitsBeforeDecimal < 0) {
            buffer.append(0);
            digitsBeforeDecimal++;
         }
      }

      do {
         buffer.append((char)(mantissa / 100000000 + 48));
         if (--digitsBeforeDecimal == 0) {
            buffer.append('.');
         }

         mantissa = mantissa % 100000000 * 10;
      } while (mantissa != 0 || digitsBeforeDecimal > 0);
   }
}
