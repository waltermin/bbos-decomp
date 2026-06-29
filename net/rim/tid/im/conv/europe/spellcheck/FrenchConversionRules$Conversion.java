package net.rim.tid.im.conv.europe.spellcheck;

class FrenchConversionRules$Conversion {
   private int _conversionType;
   static final int NO_CONV = 0;
   static final int STD_CONTRACTION_CONV = 1;
   static final int SPEC_CONTRACTION_CONV = 2;

   FrenchConversionRules$Conversion(int conversionType) {
      this._conversionType = conversionType;
   }

   int getConversionType() {
      return this._conversionType;
   }

   public int applyConversion(char[] word, int len) {
      return len;
   }

   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
   }

   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return false;
   }

   public int reverseConversion(char[] word, int offset, int len) {
      return len;
   }
}
