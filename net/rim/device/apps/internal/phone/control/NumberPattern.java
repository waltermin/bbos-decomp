package net.rim.device.apps.internal.phone.control;

class NumberPattern {
   private String _pattern;
   private boolean _rejectPattern;
   private boolean _prefixPattern;
   private boolean _unknownNumber;
   static final int REJECT = -1;
   static final int UNDECIDED = 0;
   static final int ACCEPT = 1;

   NumberPattern(String pattern) {
      this._pattern = pattern;
      int len = this._pattern.length();
      if (len > 0 && this._pattern.charAt(len - 1) == 'r') {
         this._rejectPattern = true;
         this._pattern = this._pattern.substring(0, len - 1);
         len--;
      }

      if (len > 0 && this._pattern.charAt(0) == '?') {
         this._unknownNumber = true;
         this._pattern = "";
         len = 0;
      }

      while (len > 0 && this._pattern.charAt(len - 1) == '.') {
         this._prefixPattern = true;
         this._pattern = this._pattern.substring(0, len - 1);
         len--;
      }
   }

   @Override
   public String toString() {
      String result = this._unknownNumber ? "?" : this._pattern;
      if (this._prefixPattern) {
         result = ((StringBuffer)(new Object())).append(result).append("...").toString();
      }

      if (this._rejectPattern) {
         result = ((StringBuffer)(new Object())).append(result).append('r').toString();
      }

      return result;
   }

   int test(String number) {
      if (this._unknownNumber) {
         if (number != null && number.length() != 0) {
            return 0;
         } else {
            return this._rejectPattern ? -1 : 1;
         }
      } else if (number != null && number.length() != 0) {
         while (number.startsWith("++")) {
            number = number.substring(1);
         }

         if (!this._prefixPattern && !this._rejectPattern) {
            if (!number.equals(this._pattern)) {
               return 0;
            } else {
               return this._rejectPattern ? -1 : 1;
            }
         } else if (!number.startsWith(this._pattern)) {
            return 0;
         } else {
            return this._rejectPattern ? -1 : 1;
         }
      } else {
         return 0;
      }
   }
}
