package net.rim.tid.im.conv.europe.spellcheck;

class EnglishConversionRules$IngConversion extends EnglishConversionRules$Conversion {
   EnglishConversionRules$IngConversion(int conversionType) {
      super(conversionType);
   }

   @Override
   public int applyConversion(char[] word, int offset, int len) {
      switch (super._conversionType) {
         case 2:
            throw new Object();
         case 3:
         default:
            return len - 3;
         case 4:
            word[offset + len - 3] = 'e';
            return len - 2;
         case 5:
            return len - 4;
      }
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      switch (super._conversionType) {
         case 3:
         default:
            bounds[0] = offset;
            bounds[1] = len - 3;
            return;
         case 4:
            bounds[0] = offset;
            bounds[1] = len - 2;
            return;
         case 5:
            bounds[0] = offset;
            bounds[1] = len - 4;
         case 2:
            throw new Object();
      }
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return true;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      int last = offset + len - 1;
      if ("eiEI".indexOf(word[last]) == -1 || len >= 2 && "eiEI".indexOf(word[last - 1]) != -1) {
         if ("bdfglmnprstvzBDFGLMNPRSTVZ".indexOf(word[last]) == -1) {
            EnglishConversionRules.ensureSize(word, offset + len + 3);
            word[last + 1] = 'i';
            word[last + 2] = 'n';
            word[last + 3] = 'g';
            return len + 3;
         } else if ("aeiouAEIOU".indexOf(word[last - 1]) == -1 || len > 2 && "aeiouAEIOU".indexOf(word[last - 2]) != -1) {
            EnglishConversionRules.ensureSize(word, offset + len + 3);
            word[last + 1] = 'i';
            word[last + 2] = 'n';
            word[last + 3] = 'g';
            return len + 3;
         } else {
            EnglishConversionRules.ensureSize(word, offset + len + 4);
            word[last + 1] = word[last];
            word[last + 2] = 'i';
            word[last + 3] = 'n';
            word[last + 4] = 'g';
            return len + 4;
         }
      } else {
         EnglishConversionRules.ensureSize(word, offset + len + 2);
         word[last] = 'i';
         word[last + 1] = 'n';
         word[last + 2] = 'g';
         return len + 2;
      }
   }
}
