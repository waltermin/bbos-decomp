package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.Tokenizer;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class Convert extends Value {
   protected Convert() {
   }

   public static ESObject toObject(long value) throws ThrownValue {
      switch (Value.getType(value)) {
         case -1:
            return new ESNumber(Value.getDoubleValue(value));
         case 0:
         default:
            return new ESNumber(Value.getIntegerValue(value));
         case 1:
         case 2:
            throw ThrownValue.typeError(Resources.getString(41), "undefined");
         case 3:
            throw ThrownValue.typeError(Resources.getString(41), "null");
         case 4:
            return new ESBoolean(Value.getBooleanValue(value));
         case 5:
            return new ESString(Value.getStringValue(value));
         case 6:
            return Value.getObjectValue(value);
      }
   }

   public static boolean toBoolean(long value) {
      switch (Value.getType(value)) {
         case -1:
            double d = Value.getDoubleValue(value);
            if (d != 0L && d == d) {
               return true;
            }

            return false;
         case 0:
         default:
            if (Value.getIntegerValue(value) != 0) {
               return true;
            }

            return false;
         case 1:
         case 2:
         case 3:
            return false;
         case 4:
            return Value.getBooleanValue(value);
         case 5:
            if (Value.getStringValue(value).length() != 0) {
               return true;
            }

            return false;
         case 6:
            switch (GlobalObject.getInstance().version) {
               case 100:
               case 110:
               case 120:
                  ESObject obj = Value.getObjectValue(value);
                  if (obj instanceof ESBoolean) {
                     return ((ESBoolean)obj).getValue();
                  }
               default:
                  return true;
            }
      }
   }

   static String toInternString(long value) {
      return Misc.stringIntern(toString(value));
   }

   public static String toString(long value) {
      switch (Value.getType(value)) {
         case -1:
            double d = Value.getDoubleValue(value);
            if (d == 0L) {
               return "0";
            } else {
               ParsedDouble fd = new ParsedDouble(Value.getDoubleValue(value));
               int n = fd.decExponent;
               int k = fd.nDigits;
               StringBuffer b = new StringBuffer();
               if (fd.isNegative) {
                  b.append('-');
               }

               if (fd.isExceptional) {
                  b.append(fd.digits, 0, fd.nDigits);
                  return b.toString();
               } else if (k == 0) {
                  b.append("0");
                  return b.toString();
               } else if (k <= n && n <= 21) {
                  if (fd.roundDigits(k, 17)) {
                     k = 1;
                     n++;
                  }

                  b.append(fd.digits, 0, k);

                  for (int i = n - k; i > 0; i--) {
                     b.append('0');
                  }

                  return b.toString();
               } else if (0 < n && n <= 21) {
                  b.append(fd.digits, 0, n);
                  b.append('.');
                  b.append(fd.digits, n, k - n);
                  return b.toString();
               } else if (-6 < n && n <= 0) {
                  b.append("0.");

                  for (int i = -n; i > 0; i--) {
                     b.append('0');
                  }

                  b.append(fd.digits, 0, k);
                  return b.toString();
               } else {
                  b.append(fd.digits[0]);
                  if (k != 1) {
                     b.append('.');
                     b.append(fd.digits, 1, k - 1);
                  }

                  b.append('e');
                  if (--n >= 0) {
                     b.append('+');
                  } else {
                     b.append('-');
                  }

                  b.append(Integer.toString(n < 0 ? -n : n));
                  return b.toString();
               }
            }
         case 0:
         default:
            return Integer.toString(Value.getIntegerValue(value));
         case 1:
         case 2:
            return "undefined";
         case 3:
            return "null";
         case 4:
            if (Value.getBooleanValue(value)) {
               return "true";
            }

            return "false";
         case 5:
            return Value.getStringValue(value);
         case 6:
            return toString(toStringPrimitive(value));
      }
   }

   static String escapeQuote(String str) {
      if (str.indexOf(34) == -1) {
         return str;
      }

      StringBuffer b = new StringBuffer();
      int len = str.length();

      for (int i = 0; i < len; i++) {
         char ch = str.charAt(i);
         if (ch == '"') {
            b.append('\\');
         }

         b.append(ch);
      }

      return b.toString();
   }

   static String toJoinString(long value, boolean locale) {
      switch (Value.getType(value)) {
         case 2:
         case 3:
         default:
            return "";
         case 5:
            if (GlobalObject.getInstance().version == 120) {
               return "\"" + escapeQuote(toString(value)) + "\"";
            }

            return toString(value);
         case 6:
            if (locale) {
               value = Context.callProperty(Value.getObjectValue(value), "toLocaleString", Names.NoParms);
            }
         case 1:
         case 4:
            return toString(value);
      }
   }

   public static long toPrimitive(long value) {
      return Value.getType(value) != 6 ? value : Value.getObjectValue(value).defaultValue();
   }

   public static long toNumberPrimitive(long value) {
      return Value.getType(value) != 6 ? value : Value.getObjectValue(value).defaultNumberValue();
   }

   public static long toStringPrimitive(long value) {
      return Value.getType(value) != 6 ? value : Value.getObjectValue(value).defaultStringValue();
   }

   public static long toNumber(long value) {
      switch (Value.getType(value)) {
         case -1:
            return value;
         case 0:
         default:
            return value;
         case 1:
         case 2:
            return Value.NaN;
         case 3:
            return Value.ZERO;
         case 4:
            if (Value.getBooleanValue(value)) {
               return Value.ONE;
            }

            return Value.ZERO;
         case 5:
            return toNumber(Value.getStringValue(value));
         case 6:
            return toNumber(toNumberPrimitive(value));
      }
   }

   public static double toDouble(long value) {
      value = toNumber(value);
      return Value.getType(value) == 0 ? (int)value : Value.getDoubleValue(value);
   }

   public static long toNumber(String str) {
      if (str.indexOf(0) != -1) {
         return Value.NaN;
      }

      int start = 0;
      int end = str.length();
      boolean neg = false;

      while (start != end) {
         char ch = str.charAt(start);
         if (!Tokenizer.isWhiteSpace(ch)) {
            int firstNonWhite = start;
            if (start + 1 < end) {
               if (ch == '+') {
                  ch = str.charAt(++start);
               } else if (ch == '-') {
                  ch = str.charAt(++start);
                  neg = true;
               }

               if (ch == '0' && start + 1 < end) {
                  ch = str.charAt(++start);
                  if (ch == 'x' || ch == 'X') {
                     long number = 0;
                     int i = start;

                     while (++i < end) {
                        int digit = Tokenizer.hexValue(str.charAt(i));
                        if (digit == -1) {
                           break;
                        }

                        number = (number << 4) + digit;
                     }

                     int last = i;

                     while (i < end) {
                        if (!Tokenizer.isWhiteSpace(str.charAt(i))) {
                           return Value.NaN;
                        }

                        i++;
                     }

                     if (last - start < 12) {
                        return Value.makeDoubleValue(neg ? -number : number);
                     }

                     double d = Tokenizer.convertBinary(str, start + 1, last, 16);
                     return Value.makeDoubleValue(neg ? -d : d);
                  }

                  start--;
               }

               while (Tokenizer.isWhiteSpace(str.charAt(--end))) {
               }

               end++;
            }

            if (end - start == 8 && ch == 'I' && str.substring(start, end).equals("Infinity")) {
               if (neg) {
                  return Value.NEGATIVE_INFINITY;
               }

               return Value.POSITIVE_INFINITY;
            }

            try {
               str = str.substring(firstNonWhite, end);
               return Value.makeDoubleValue(Double.valueOf(str));
            } finally {
               ;
            }
         }

         start++;
      }

      return Value.ZERO;
   }

   public static int toInt32(long value) {
      if (Value.getType(value) == 0) {
         return (int)value;
      } else {
         double d = toDouble(value);
         if (d != d) {
            return 0;
         } else if (d == 9218868437227405312L) {
            return 0;
         } else {
            return d == -4503599627370496L ? 0 : (int)((long)d);
         }
      }
   }

   public static long toUint32(long value) {
      return toInt32(value) & 4294967295L;
   }

   public static int toUint16(long value) {
      return (char)toInt32(value);
   }

   static int toInteger(long value) {
      if (Value.getType(value) == 0) {
         return (int)value;
      } else {
         double d = toDouble(value);
         if (d != d) {
            return 0;
         } else if (d > 4746794007244308480L) {
            return Integer.MAX_VALUE;
         } else {
            return d < -4476578029606273024L ? Integer.MIN_VALUE : (int)((long)d);
         }
      }
   }

   static int toSign(long value) {
      if (Value.getType(value) == 0) {
         return (int)value;
      } else {
         double d = toDouble(value);
         if (d < 0L) {
            return -1;
         } else {
            return d > 0L ? 1 : 0;
         }
      }
   }

   static ESRegExp toRegExp(long value) {
      ESObject obj = Value.checkIfObjectValue(value);
      if (obj != null && obj instanceof ESRegExp) {
         return (ESRegExp)obj;
      } else {
         return Value.getType(value) == 2 ? new ESRegExp("") : new ESRegExp(toString(value));
      }
   }
}
