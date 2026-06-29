package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.util.CharacterUtilities;

class EnglishConversionRules$PluralConversion extends EnglishConversionRules$Conversion {
   boolean upperCase;

   EnglishConversionRules$PluralConversion(int conversionType) {
      super(conversionType);
   }

   @Override
   public int applyConversion(char[] word, int offset, int len) {
      this.upperCase = CharacterUtilities.isUpperCase(word[offset + len - 1]);
      switch (super._conversionType) {
         case 5:
            throw new IllegalStateException();
         case 6:
         default:
            return len - 1;
         case 7:
            return len - 2;
         case 8:
            word[offset + len - 3] = 'y';
            return len - 2;
         case 9:
            return len - 1;
      }
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      switch (super._conversionType) {
         case 5:
            throw new IllegalStateException();
         case 6:
         default:
            bounds[0] = offset;
            bounds[1] = len - 1;
            return;
         case 7:
            bounds[0] = offset;
            bounds[1] = len - 2;
            return;
         case 8:
            bounds[0] = offset;
            bounds[1] = len - 2;
            return;
         case 9:
            bounds[0] = offset;
            bounds[1] = len - 1;
      }
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return true;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      int last = offset + len - 1;
      if ("sxzSXZ".indexOf(word[last]) == -1 && (word[last] != 'h' || "csCS".indexOf(word[last - 1]) == -1)) {
         if (word[last] != 'y') {
            EnglishConversionRules.ensureSize(word, offset + len + 1);
            word[last + 1] = 's';
            return len + 1;
         } else if ("aeiouAEIOU".indexOf(word[last - 1]) != -1) {
            EnglishConversionRules.ensureSize(word, offset + len + 1);
            word[last + 1] = 's';
            return len + 1;
         } else {
            EnglishConversionRules.ensureSize(word, offset + len + 2);
            word[last] = 'i';
            word[last + 1] = 'e';
            word[last + 2] = 's';
            return len + 2;
         }
      } else {
         EnglishConversionRules.ensureSize(word, offset + len + 2);
         word[last + 1] = 'e';
         word[last + 2] = 's';
         return len + 2;
      }
   }
}
