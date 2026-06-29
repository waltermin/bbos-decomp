package net.rim.tid.im.conv.europe.spellcheck;

class FrenchConversionRules$QUConversion extends FrenchConversionRules$Conversion {
   FrenchConversionRules$QUConversion() {
      super(2);
   }

   @Override
   public int applyConversion(char[] word, int len) {
      System.arraycopy(word, 3, word, 0, len - 3);
      return len - 3;
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      bounds[0] = offset + 3;
      bounds[1] = len - 3;
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return "aeiouh".indexOf(word[offset]) != -1;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      System.arraycopy(word, offset, word, offset + 3, len);
      word[offset] = 'q';
      word[offset + 1] = 'u';
      word[offset + 2] = '\'';
      return len + 3;
   }
}
