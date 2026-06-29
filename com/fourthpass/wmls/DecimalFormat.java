package com.fourthpass.wmls;

import java.util.Vector;
import net.rim.device.api.util.NumberUtilities;

final class DecimalFormat {
   private int width = -1;
   private int precision = -1;
   private char type;
   private Vector substrings = (Vector)(new Object());
   private int fmtidx;
   private int count;
   private boolean firstPlain;
   private static final char ESCAPE;

   public DecimalFormat(String s) {
      int i = s.length();
      boolean escaped = false;
      int j = 0;
      char[] ac = new char[i];
      if (i != 0) {
         this.count = 1;
         if (s.charAt(0) == '%') {
            escaped = true;
         } else {
            this.firstPlain = true;
         }

         for (int k = escaped ? 1 : 0; k < i; k++) {
            char c = s.charAt(k);
            if (c == '%' && j > 0) {
               String s1 = (String)(new Object(ac, 0, j));
               this.substrings.addElement(s1);
               this.count++;
               j = 0;
            } else {
               ac[j++] = s.charAt(k);
               if (k == i - 1) {
                  this.substrings.addElement(new Object(ac, 0, j));
                  this.count++;
               }
            }
         }

         boolean initiated = false;
         escaped = this.firstPlain;

         for (int l = escaped ? 1 : 0; l < this.count - 1; l++) {
            String string = (String)this.substrings.elementAt(l);
            if (string.charAt(0) != '%') {
               int parsed;
               if (!initiated) {
                  parsed = this.parse(string, false);
                  this.fmtidx = l;
                  initiated = true;
               } else {
                  parsed = this.parse(string, true);
               }

               this.substrings.setElementAt(string.substring(parsed), l);
            } else {
               this.type = '%';
            }
         }
      }
   }

   public final String format(int i) {
      switch (this.type) {
         case '%':
            return this.glueStrings("");
         case 'd':
            return this._format(i);
         case 'f':
            return this._format((float)i);
         case 's':
            return this._format(String.valueOf(i));
         default:
            return null;
      }
   }

   public final String format(String s) {
      switch (this.type) {
         case '%':
            return this.glueStrings("");
         case 'd':
            return this._format(Integer.valueOf(s));
         case 'f':
            return this._format(Float.valueOf(s));
         case 's':
            return this._format(s);
         default:
            return null;
      }
   }

   public final String format(float f) {
      switch (this.type) {
         case '%':
            return this.glueStrings("");
         case 'd':
            return this._format((int)f);
         case 'f':
            return this._format(f);
         case 's':
            return this._format(Float.toString(f));
         default:
            return null;
      }
   }

   public final String format(boolean flag) {
      switch (this.type) {
         case '%':
            return this.glueStrings("");
         case 'd':
            return this._format(flag ? 1 : 0);
         case 'f':
            return this._format((float)(flag ? 1065353216 : 0));
         case 's':
            return this._format(flag ? "true" : "false");
         default:
            return null;
      }
   }

   private final String glueStrings(String s) {
      StringBuffer stringbuffer = (StringBuffer)(new Object());

      for (int i = 0; i < this.count - 1; i++) {
         if (i == this.fmtidx) {
            stringbuffer.append(s);
         }

         if (this.substrings.elementAt(i) != null) {
            stringbuffer.append((String)this.substrings.elementAt(i));
         }
      }

      return stringbuffer.toString();
   }

   private final int parse(String s, boolean dontSave) {
      char[] ac = new char[20];
      char[] ac1 = new char[20];
      int i = 0;
      int j = 0;
      int k = 0;

      int l;
      for (l = 0; l < s.length() && k != 3; l++) {
         char c = s.charAt(l);
         switch (k) {
            case -1:
               break;
            case 0:
            default:
               if (c == '%') {
                  l--;
                  k = 3;
                  break;
               } else {
                  k = 1;
               }
            case 1:
               if (c >= '0' && c <= '9') {
                  ac[i++] = c;
               } else if (c == '.') {
                  k = 2;
               } else {
                  if (c != 'd' && c != 's' && c != 'f') {
                     throw new Object();
                  }

                  this.type = c;
                  k = 3;
               }
               break;
            case 2:
               if (c >= '0' && c <= '9') {
                  ac1[j++] = c;
               } else {
                  if (c != 'd' && c != 's' && c != 'f') {
                     throw new Object();
                  }

                  this.type = c;
                  if (j == 0) {
                     ac1[0] = '0';
                     j = 1;
                  }

                  k = 3;
               }
         }
      }

      if (!dontSave) {
         if (i > 0) {
            this.width = Integer.parseInt(((String)(new Object(ac))).trim());
         }

         if (j > 0) {
            this.precision = Integer.parseInt(((String)(new Object(ac1))).trim());
         }
      }

      return l;
   }

   private final String _format(float f) {
      if (this.width == 0) {
         return "";
      }

      int i = this.precision >= 0 ? this.precision : 6;
      StringBuffer stringbuffer = (StringBuffer)(new Object());
      NumberUtilities.appendNumber(stringbuffer, (int)f, 10);
      if (i > 0) {
         stringbuffer.append('.');
         float tempFloat = f - (int)f;

         for (int k = 0; k < i; k++) {
            tempFloat *= 1092616192;
         }

         NumberUtilities.appendNumber(stringbuffer, (int)tempFloat, 10, this.precision);
      }

      while (stringbuffer.length() < this.width) {
         stringbuffer.insert(0, ' ');
      }

      return this.glueStrings(stringbuffer.toString());
   }

   private final String _format(int i) {
      int j = this.precision != -1 ? this.precision : 1;
      String s = String.valueOf(i);
      if (j == 0 && i == 0) {
         return this.glueStrings("");
      }

      int k = s.length();
      int l = k;
      if (i < 0) {
         l--;
      }

      j = l <= j ? j : l;
      int stringWidth = j;
      if (i < 0) {
         stringWidth++;
      }

      int actualWidth = stringWidth <= this.width ? this.width : stringWidth;
      StringBuffer stringbuffer = (StringBuffer)(new Object(actualWidth));
      int stringMinor = stringWidth - l;
      int widthDif = actualWidth - stringWidth;

      for (int var12 = 0; var12 < widthDif; var12++) {
         stringbuffer.insert(var12, ' ');
      }

      for (int var13 = widthDif; var13 < widthDif + stringMinor; var13++) {
         stringbuffer.insert(var13, '0');
      }

      if (i < 0) {
         stringbuffer.setCharAt(widthDif, '-');
      }

      stringbuffer.append(s.substring(i >= 0 ? 0 : 1, k));
      return this.glueStrings(stringbuffer.toString());
   }

   private final String _format(String string) {
      String s;
      if (this.precision == -1) {
         s = string;
      } else {
         s = string.substring(0, this.precision);
      }

      int widthDiff = this.width - s.length();
      if (widthDiff > 0 && (this.precision == -1 || this.width <= this.precision)) {
         StringBuffer sb = (StringBuffer)(new Object());

         for (int j = 0; j < widthDiff; j++) {
            sb.append(' ');
         }

         sb.append(s);
         s = sb.toString();
      }

      return this.glueStrings(s);
   }
}
