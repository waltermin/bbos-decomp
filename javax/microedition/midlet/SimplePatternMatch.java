package javax.microedition.midlet;

class SimplePatternMatch {
   private int _offset;
   private String _candidate;
   private String _pattern;
   private int _requiredCount;
   private boolean _gteq;

   public SimplePatternMatch(String pattern) {
      this._pattern = pattern;
   }

   public boolean match(String candidate) {
      this._candidate = candidate;
      this._offset = 0;
      this._requiredCount = 0;
      this._gteq = false;
      String pattern = this._pattern;
      if (!pattern.equals("*") && !pattern.equals(candidate)) {
         if (candidate != null && candidate.length() != 0) {
            int len = pattern.length();
            char[] pchars = new char[len];
            pattern.getChars(0, len, pchars, 0);
            int candidatestringlength = candidate.length();
            int patternlength = pchars.length;
            StringBuffer region = new StringBuffer();
            int anchor = 0;

            for (int i = 0; i < patternlength; i++) {
               char p = pchars[i];
               switch (p) {
                  case '*':
                  case '?':
                     if (region.length() > 0) {
                        if (!this.matchRegion(region, anchor)) {
                           return false;
                        }

                        region.delete(0, region.length());
                     }

                     anchor = -1;
                     if (p == '?') {
                        this._requiredCount++;
                     }

                     if (p == '*') {
                        this._gteq = true;
                     }
                     break;
                  default:
                     region.append(p);
               }
            }

            if (region.length() > 0) {
               anchor = candidatestringlength - region.length();
               if (!this.matchRegion(region, anchor)) {
                  return false;
               }
            }

            return this.hasRequiredCharCount();
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean matchRegion(StringBuffer region, int anchor) {
      int regionlength = region.length();
      if (this._offset + regionlength > this._candidate.length()) {
         return false;
      }

      int offset = this._offset;
      if (anchor != -1) {
         offset = anchor;
      }

      int index = this._candidate.indexOf(region.toString(), offset);
      if (this._requiredCount > 0) {
         if (this._gteq && this._requiredCount > index - this._offset) {
            return false;
         }

         if (!this._gteq && this._requiredCount != index - this._offset) {
            return false;
         }
      } else if (index == -1) {
         return false;
      }

      this._offset = index + regionlength;
      this._requiredCount = 0;
      this._gteq = false;
      return true;
   }

   private boolean hasRequiredCharCount() {
      if (this._requiredCount == 0) {
         return true;
      }

      int l = this._candidate.substring(this._offset).length();
      return this._gteq ? l >= this._requiredCount : l == this._requiredCount;
   }
}
