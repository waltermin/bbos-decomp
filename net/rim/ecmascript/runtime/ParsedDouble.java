package net.rim.ecmascript.runtime;

class ParsedDouble {
   public char[] digits = new char[50];
   public int nDigits;
   public int decExponent;
   public boolean isNegative;
   public boolean isExceptional;

   ParsedDouble(double d) {
      String str = Double.toString(d);
      int decimalPosition = -1;
      int length = str.length();
      int i = 0;
      if (str.charAt(i) == '-') {
         this.isNegative = true;
         i++;
      }

      char ch = str.charAt(i);
      if (ch <= '9' && ch >= '0') {
         label83:
         for (; i < length; i++) {
            ch = str.charAt(i);
            switch (ch) {
               case '.':
                  decimalPosition = i;
                  break;
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
                  this.digits[this.nDigits++] = ch;
                  break;
               case 'E':
                  ch = str.charAt(++i);
                  boolean negExp = false;
                  if (ch == '-') {
                     negExp = true;
                     i++;
                  } else if (ch == '+') {
                     i++;
                  }

                  while (i < length) {
                     ch = str.charAt(i);
                     this.decExponent = this.decExponent * 10 + ch - 48;
                     i++;
                  }

                  if (negExp) {
                     this.decExponent = -this.decExponent;
                  }
                  break label83;
            }
         }
      } else {
         for (this.isExceptional = true; i < length; i++) {
            this.digits[this.nDigits++] = str.charAt(i);
         }
      }

      if (decimalPosition != -1) {
         if (this.isNegative) {
            decimalPosition--;
         }

         this.decExponent += decimalPosition;
      } else {
         this.decExponent = this.decExponent + this.nDigits;
      }

      int k = this.nDigits;

      for (int var10 = this.nDigits - 1; var10 >= 0 && this.digits[var10] == '0'; var10--) {
         k--;
      }

      this.nDigits = k;

      for (int var11 = 0; var11 != this.nDigits; var11++) {
         if (this.digits[var11] != '0') {
            this.nDigits -= var11;
            this.decExponent -= var11;
            System.arraycopy(this.digits, var11, this.digits, 0, this.nDigits);
            return;
         }
      }

      this.nDigits = 1;
   }

   boolean roundDigits(int len, int roundLen) {
      if (len <= roundLen) {
         return false;
      }

      int carry = 0;
      if (this.digits[roundLen] >= '5') {
         carry = 1;

         for (int i = roundLen - 1; i >= 0; i--) {
            if (this.digits[i] != '9') {
               this.digits[i]++;
               carry = 0;
               break;
            }

            this.digits[i] = '0';
            carry = 1;
         }
      }

      this.nDigits = roundLen;

      for (int i = roundLen; i < len; i++) {
         this.digits[i] = '0';
      }

      if (carry != 0) {
         this.digits[0] = '1';
         this.nDigits = 1;
         this.decExponent++;
         return true;
      } else {
         return false;
      }
   }
}
