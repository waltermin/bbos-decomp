package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.Tokenizer;

class GlobalProperties$10 extends HostFunction {
   GlobalProperties$10(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      String str = Convert.toString(this.getParm(0));
      int start = 0;
      int length = str.length();
      if (length == 0) {
         return Value.NaN;
      }

      while (Tokenizer.isWhiteSpace(str.charAt(start))) {
         start++;
      }

      length -= start;
      int sign = 1;
      int end = start;
      if (length == 0) {
         return Value.NaN;
      }

      if (length > 0) {
         char ch = str.charAt(end);
         if (ch == '-') {
            end++;
            length--;
            sign = -1;
         } else if (ch == '+') {
            end++;
            length--;
         }
      }

      int r = Convert.toInt32(this.getParm(1));
      if (r == 0) {
         r = 10;
         if (length > 0 && str.charAt(end) == '0') {
            end++;
            if (--length > 1) {
               char ch = str.charAt(end);
               if (ch != 'x' && ch != 'X') {
                  r = 8;
               } else {
                  r = 16;
                  end++;
                  length--;
               }
            }
         }
      } else if (r == 16 && length > 0 && str.charAt(end) == '0' && length > 2) {
         char ch = str.charAt(end + 1);
         if (ch == 'x' || ch == 'X') {
            end += 2;
            length -= 2;
         }
      }

      if (r >= 2 && r <= 36) {
         int maxLower;
         int maxUpper;
         int maxDig;
         if (r < 10) {
            maxDig = 48 + r - 1;
            maxLower = 97;
            maxUpper = 65;
         } else {
            maxDig = 57;
            maxLower = 97 + r - 10;
            maxUpper = 65 + r - 10;
         }

         int sum = 0;
         double dsum = (double)0L;
         boolean overflow = false;
         int maxIntSum = 59652322;
         int first = end;

         while (--length >= 0) {
            char ch = str.charAt(end);
            int digitValue;
            if (ch >= '0' && ch <= maxDig) {
               digitValue = ch - '0';
            } else if (ch >= 'a' && ch < maxLower) {
               digitValue = ch - 'a' + 10;
            } else {
               if (ch < 'A' || ch >= maxUpper) {
                  if (end == first) {
                     return Value.NaN;
                  }
                  break;
               }

               digitValue = ch - 'A' + 10;
            }

            end++;
            if (overflow) {
               dsum = r * dsum + digitValue;
            } else if (sum >= maxIntSum) {
               overflow = true;
               dsum = (double)r * sum + digitValue;
            } else {
               sum = r * sum + digitValue;
            }
         }

         if (overflow) {
            switch (r) {
               case 2:
               case 4:
               case 8:
               case 16:
               case 32:
                  return Value.makeDoubleValue(sign * Tokenizer.convertBinary(str, first, end, r));
               case 10:
                  return Value.makeDoubleValue(sign * Double.parseDouble(str.substring(start, end)));
               default:
                  return Value.makeDoubleValue(sign * dsum);
            }
         } else {
            return Value.makeIntegerValue(sign * sum);
         }
      } else {
         return Value.NaN;
      }
   }
}
