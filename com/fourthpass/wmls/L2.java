package com.fourthpass.wmls;

import java.util.Vector;

final class L2 extends Lib {
   @Override
   public final Value invoke(int func, Interpreter$Engine engine) {
      switch (func) {
         case -1:
            throw new Object("Invalid Function Id");
         case 0:
         default:
            return length(engine.popStack());
         case 1:
            return isEmpty(engine.popStack());
         case 2:
            return charAt(engine.popStack(), engine.popStack());
         case 3:
            return subString(engine.popStack(), engine.popStack(), engine.popStack());
         case 4:
            return find(engine.popStack(), engine.popStack());
         case 5:
            return replace(engine.popStack(), engine.popStack(), engine.popStack());
         case 6:
            return elements(engine.popStack(), engine.popStack());
         case 7:
            return elementAt(engine.popStack(), engine.popStack(), engine.popStack());
         case 8:
            return removeAt(engine.popStack(), engine.popStack(), engine.popStack());
         case 9:
            return replaceAt(engine.popStack(), engine.popStack(), engine.popStack(), engine.popStack());
         case 10:
            return insertAt(engine.popStack(), engine.popStack(), engine.popStack(), engine.popStack());
         case 11:
            return squeeze(engine.popStack());
         case 12:
            return trim(engine.popStack());
         case 13:
            return compare(engine.popStack(), engine.popStack());
         case 14:
            return toString(engine.popStack());
         case 15:
            return format(engine.popStack(), engine.popStack());
      }
   }

   public static final Value length(Value string) {
      string = string.toStringValue();
      return string.isString() ? new IntegerValue(((StringValue)string).getString().length()) : Value.INVALID;
   }

   public static final Value isEmpty(Value string) {
      string = string.toStringValue();
      if (string.isString()) {
         int i = ((StringValue)string).getString().length();
         return i == 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
      } else {
         return Value.INVALID;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Value charAt(Value index, Value string) {
      string = string.toStringValue();
      Value intIndex;
      if (index.isFloat()) {
         intIndex = ((FloatValue)index).toInt();
      } else {
         intIndex = index.toIntegerValue();
      }

      if (string.isString() && intIndex.isInteger()) {
         String s = ((StringValue)string).getString();
         int i = ((IntegerValue)intIndex).getValue();
         char[] ac = new char[1];
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            ac[0] = s.charAt(i);
            var8 = false;
         } finally {
            if (var8) {
               return StringValue.EMPTY_STRING.clone();
            }
         }

         return new StringValue((String)(new Object(ac)));
      } else {
         return Value.INVALID;
      }
   }

   public static final Value subString(Value length, Value startIndex, Value string) {
      string = string.toStringValue();
      Value intIndex;
      if (startIndex.isFloat()) {
         intIndex = ((FloatValue)startIndex).toInt();
      } else {
         intIndex = startIndex.toIntegerValue();
      }

      Value intlength;
      if (length.isFloat()) {
         intlength = ((FloatValue)length).toInt();
      } else {
         intlength = length.toIntegerValue();
      }

      if (intIndex.isInteger() && ((IntegerValue)intIndex).getValue() < 0) {
         intIndex = new IntegerValue(0);
      }

      if (string.isString() && intIndex.isInteger() && intlength.isInteger()) {
         String s = ((StringValue)string).getString();
         int i = ((IntegerValue)intIndex).getValue();
         int j = ((IntegerValue)intlength).getValue();
         int k = s.length();
         if (i < 0) {
            i = 0;
         }

         if (j >= k - i) {
            j = k - i;
         }

         int l = i + j;

         try {
            return new StringValue(s.substring(i, l));
         } finally {
            return StringValue.EMPTY_STRING.clone();
         }
      } else {
         return Value.INVALID;
      }
   }

   public static final Value find(Value subString, Value string) {
      subString = subString.toStringValue();
      string = string.toStringValue();
      if (string.isString() && subString.isString()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)subString).getString();
         return s1.length() == 0 ? Value.INVALID : new IntegerValue(s.indexOf(s1));
      } else {
         return Value.INVALID;
      }
   }

   public static final Value replace(Value newSubString, Value oldSubString, Value string) {
      newSubString = newSubString.toStringValue();
      oldSubString = oldSubString.toStringValue();
      string = string.toStringValue();
      if (string.isString() && oldSubString.isString() && newSubString.isString()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)oldSubString).getString();
         int i = s1.length();
         if (i == 0) {
            return Value.INVALID;
         }

         String s2 = ((StringValue)newSubString).getString();
         Vector vector = (Vector)(new Object());
         int k = 0;
         int l = 0;

         while (true) {
            int j = s.indexOf(s1, l);
            if (j == -1) {
               StringBuffer stringbuffer = (StringBuffer)(new Object(s.length()));
               int i1 = 0;

               for (int j1 = 0; j1 < s.length(); j1++) {
                  if (i1 < k && j1 == vector.elementAt(i1)) {
                     j1 += i - 1;
                     i1++;
                     stringbuffer.append(s2);
                  } else {
                     stringbuffer.append(s.charAt(j1));
                  }
               }

               return new StringValue(stringbuffer.toString());
            }

            k++;
            vector.addElement(new Object(j));
            l = j + 1;
         }
      } else {
         return Value.INVALID;
      }
   }

   public static final Value elements(Value separator, Value string) {
      separator = separator.toStringValue();
      string = string.toStringValue();
      if (separator.isString() && string.isString()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)separator).getString();
         if (s1.length() == 0) {
            return Value.INVALID;
         }

         int i = getTokenCount(s, s1);
         return i <= 0 ? new IntegerValue(1) : new IntegerValue(i);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value elementAt(Value separator, Value index, Value string) {
      separator = separator.toStringValue();
      string = string.toStringValue();
      Value intIndex;
      if (index.isFloat()) {
         intIndex = ((FloatValue)index).toInt();
      } else {
         intIndex = index.toIntegerValue();
      }

      if (string.isString() && separator.isString() && intIndex.isInteger()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)separator).getString();
         if (s1.length() == 0) {
            return Value.INVALID;
         }

         if (s.length() == 0) {
            return new StringValue("");
         }

         String[] as = makeTokens(s, s1);
         int i = as.length;
         int j = ((IntegerValue)intIndex).getValue();
         j = j >= 0 ? j : 0;
         j = j < i ? j : i - 1;
         String s2 = as[j];
         return new StringValue(s2);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value removeAt(Value separator, Value index, Value string) {
      separator = separator.toStringValue();
      string = string.toStringValue();
      Value intIndex;
      if (index.isFloat()) {
         intIndex = ((FloatValue)index).toInt();
      } else {
         intIndex = index.toIntegerValue();
      }

      if (string.isString() && separator.isString() && intIndex.isInteger()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)separator).getString();
         if (s1.length() == 0) {
            return Value.INVALID;
         }

         if (s.length() == 0) {
            return new StringValue("");
         }

         String[] as = makeTokens(s, s1);
         int i = getTokenCount(s, s1);
         int j = as.length;
         int k = ((IntegerValue)intIndex).getValue();
         k = k >= 0 ? k : 0;
         k = k < i ? k : i - 1;
         String s2 = "";
         if (k == i - 1) {
            for (int l = 0; l < j - 1; l++) {
               if (l != 0) {
                  s2 = ((StringBuffer)(new Object())).append(s2).append(s1.substring(0, 1)).toString();
               }

               s2 = ((StringBuffer)(new Object())).append(s2).append(as[l]).toString();
            }
         } else {
            for (int i1 = 0; i1 < j; i1++) {
               if (i1 != k) {
                  s2 = ((StringBuffer)(new Object())).append(s2).append(as[i1]).toString();
                  if (i1 + 1 < j) {
                     s2 = ((StringBuffer)(new Object())).append(s2).append(s1.substring(0, 1)).toString();
                  }
               }
            }
         }

         return new StringValue(s2);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value replaceAt(Value separator, Value index, Value element, Value string) {
      separator = separator.toStringValue();
      element = element.toStringValue();
      string = string.toStringValue();
      Value intIndex;
      if (index.isFloat()) {
         intIndex = ((FloatValue)index).toInt();
      } else {
         intIndex = index.toIntegerValue();
      }

      if (string.isString() && element.isString() && separator.isString() && intIndex.isInteger()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)separator).getString();
         if (s1.length() == 0) {
            return Value.INVALID;
         }

         String s2 = ((StringValue)element).getString();
         if (s.length() == 0) {
            return new StringValue(s2);
         }

         String[] as = makeTokens(s, s1);
         int i = getTokenCount(s, s1);
         int j = as.length;
         int k = ((IntegerValue)intIndex).getValue();
         k = k >= 0 ? k : 0;
         k = k < i ? k : i - 1;
         String s3 = "";

         for (int l = 0; l < j; l++) {
            if (l == k) {
               s3 = ((StringBuffer)(new Object())).append(s3).append(s2).toString();
            } else {
               s3 = ((StringBuffer)(new Object())).append(s3).append(as[l]).toString();
            }

            if (l + 1 < j) {
               s3 = ((StringBuffer)(new Object())).append(s3).append(s1.substring(0, 1)).toString();
            }
         }

         return new StringValue(s3);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value insertAt(Value separator, Value index, Value element, Value string) {
      separator = separator.toStringValue();
      element = element.toStringValue();
      string = string.toStringValue();
      Value intIndex;
      if (index.isFloat()) {
         intIndex = ((FloatValue)index).toInt();
      } else {
         intIndex = index.toIntegerValue();
      }

      if (string.isString() && element.isString() && separator.isString() && intIndex.isInteger()) {
         String s = ((StringValue)string).getString();
         String s1 = ((StringValue)element).getString();
         String s2 = ((StringValue)separator).getString();
         if (s2.length() == 0) {
            return Value.INVALID;
         }

         if (s.length() == 0) {
            return new StringValue(s1);
         }

         String[] as = makeTokens(s, s2);
         int i = getTokenCount(s, s2);
         int j = as.length;
         int k = ((IntegerValue)intIndex).getValue();
         k = k >= 0 ? k : 0;
         String s3 = "";
         if (k >= i) {
            s3 = ((StringBuffer)(new Object())).append(s).append(s2.substring(0, 1)).append(s1).toString();
         } else if (k == 0) {
            s3 = ((StringBuffer)(new Object())).append(s1).append(s2.substring(0, 1)).append(s).toString();
         } else {
            for (int l = 0; l < j; l++) {
               if (l == k) {
                  s3 = ((StringBuffer)(new Object())).append(s3).append(s1).append(s2.substring(0, 1)).toString();
               }

               s3 = ((StringBuffer)(new Object())).append(s3).append(as[l]).toString();
               if (l + 1 < j) {
                  s3 = ((StringBuffer)(new Object())).append(s3).append(s2.substring(0, 1)).toString();
               }
            }
         }

         return new StringValue(s3);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value squeeze(Value string) {
      string = string.toStringValue();
      if (!string.isString()) {
         return Value.INVALID;
      }

      String s = ((StringValue)string).getString();
      StringBuffer stringbuffer = (StringBuffer)(new Object(s.length()));
      boolean flag = false;

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (flag) {
            if (c == ' ') {
               continue;
            }

            flag = false;
         }

         if (c == ' ') {
            flag = true;
         }

         stringbuffer.append(c);
      }

      return new StringValue(stringbuffer.toString());
   }

   public static final Value trim(Value string) {
      string = string.toStringValue();
      if (string.isString()) {
         String s = ((StringValue)string).toString();
         String s1 = s.trim();
         return new StringValue(s1);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value compare(Value string1, Value string2) {
      string1 = string1.toStringValue();
      string2 = string2.toStringValue();
      if (string1.isString() && string2.isString()) {
         String s = ((StringValue)string1).getString();
         String s1 = ((StringValue)string2).getString();
         int i = s1.compareTo(s);
         i = i != 0 ? i / Math.abs(i) : 0;
         return new IntegerValue(i);
      } else {
         return Value.INVALID;
      }
   }

   public static final Value toString(Value value) {
      return value.isInvalid() ? new StringValue(value.toString()) : value.toStringValue().clone();
   }

   public static final Value format(Value value, Value format) {
      format = format.toStringValue();

      try {
         if (format.isString() && !value.isInvalid()) {
            DecimalFormat df = new DecimalFormat(((StringValue)format).getString());
            String s1;
            if (value.isInteger()) {
               s1 = df.format(((IntegerValue)value).getValue());
            } else if (value.isFloat()) {
               s1 = df.format(((FloatValue)value).getValue());
            } else if (value.isString()) {
               s1 = df.format(((StringValue)value).getString());
            } else {
               s1 = df.format(((BooleanValue)value).getValue());
            }

            return new StringValue(s1);
         }
      } finally {
         ;
      }

      return Value.INVALID;
   }

   private static final String[] makeTokens(String s, String s1) {
      String[] as = new String[]{""};
      int i = getTokenCount(s, s1);
      if (i == 0) {
         return as;
      }

      String[] as1 = new Object[i];
      char c = s1.charAt(0);
      int j = s.length();
      int k = 0;
      int l = 0;

      for (int i1 = 0; i1 < j; i1++) {
         if (s.charAt(i1) == c) {
            as1[l] = s.substring(k, i1);
            k = i1 + 1;
            l++;
         }
      }

      as1[l] = s.substring(k, j);
      return as1;
   }

   private static final int getTokenCount(String s, String s1) {
      if (s1 != null && s1.length() != 0 && s != null) {
         if (s.length() == 0) {
            return 0;
         }

         char c = s1.charAt(0);
         int i = s.length();
         int j = 1;

         for (int k = 0; k < i; k++) {
            if (s.charAt(k) == c) {
               j++;
            }
         }

         return j;
      } else {
         return -1;
      }
   }
}
