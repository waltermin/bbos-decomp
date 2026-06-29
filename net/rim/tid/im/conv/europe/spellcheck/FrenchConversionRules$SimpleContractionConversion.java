package net.rim.tid.im.conv.europe.spellcheck;

class FrenchConversionRules$SimpleContractionConversion extends FrenchConversionRules$Conversion {
   private char _contractionCharacter;

   FrenchConversionRules$SimpleContractionConversion() {
      super(1);
   }

   void setContractionCharacter(char contractionCharacter) {
      this._contractionCharacter = contractionCharacter;
   }

   @Override
   public int applyConversion(char[] word, int len) {
      System.arraycopy(word, 2, word, 0, len - 2);
      return len - 2;
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      bounds[0] = offset + 2;
      bounds[1] = len - 2;
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return "aeiouh".indexOf(word[offset]) != -1;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      System.arraycopy(word, offset, word, offset + 2, len);
      word[offset] = this._contractionCharacter;
      word[offset + 1] = '\'';
      return len + 2;
   }
}
