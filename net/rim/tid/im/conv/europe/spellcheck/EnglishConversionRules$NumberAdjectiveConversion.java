package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.util.CharacterUtilities;

class EnglishConversionRules$NumberAdjectiveConversion extends EnglishConversionRules$Conversion {
   EnglishConversionRules$NumberAdjectiveConversion(int conversionType) {
      super(conversionType);
   }

   @Override
   public int applyConversion(char[] word, int offset, int len) {
      return len - 2;
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      bounds[0] = offset;
      bounds[1] = len - 2;
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      for (int i = offset; i < offset + len; i++) {
         if (!CharacterUtilities.isDigit(word[i])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      int last = offset + len - 1;
      switch (word[last]) {
         case '0':
            return this.reverseThConversion(word, offset, len);
         case '1':
         default:
            if (len > 1 && word[last - 1] == '1') {
               return this.reverseThConversion(word, offset, len);
            }

            EnglishConversionRules.ensureSize(word, offset + len + 2);
            word[last + 1] = 's';
            word[last + 2] = 't';
            return len + 2;
         case '2':
            if (len > 1 && word[last - 1] == '1') {
               return this.reverseThConversion(word, offset, len);
            }

            word[last + 1] = 'n';
            word[last + 2] = 'd';
            return len + 2;
         case '3':
            if (len > 1 && word[last - 1] == '1') {
               return this.reverseThConversion(word, offset, len);
            } else {
               EnglishConversionRules.ensureSize(word, offset + len + 2);
               word[last + 1] = 'r';
               word[last + 2] = 'd';
               return len + 2;
            }
      }
   }

   private int reverseThConversion(char[] word, int offset, int len) {
      int last = offset + len - 1;
      EnglishConversionRules.ensureSize(word, offset + len + 2);
      word[last + 1] = 't';
      word[last + 2] = 'h';
      return len + 2;
   }
}
