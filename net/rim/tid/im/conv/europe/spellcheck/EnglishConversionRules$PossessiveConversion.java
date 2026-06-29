package net.rim.tid.im.conv.europe.spellcheck;

class EnglishConversionRules$PossessiveConversion extends EnglishConversionRules$Conversion {
   EnglishConversionRules$PossessiveConversion() {
      super(1);
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
      return "s'S".indexOf(word[offset + len - 1]) == -1;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      int index = offset + len;
      EnglishConversionRules.ensureSize(word, offset + len + 2);
      word[index++] = '\'';
      if (word[offset + len - 1] != 's') {
         word[index++] = 's';
         return len + 2;
      } else {
         return len + 1;
      }
   }
}
