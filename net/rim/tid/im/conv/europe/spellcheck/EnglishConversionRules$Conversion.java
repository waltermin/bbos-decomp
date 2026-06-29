package net.rim.tid.im.conv.europe.spellcheck;

class EnglishConversionRules$Conversion {
   protected int _conversionType;
   static final int NO_CONV = 0;
   static final int SIMPLE_POSSESSIVE_CONV = 1;
   static final int PLURAL_POSSESSIVE_CONV = 2;
   static final int SIMPLE_ING_CONV = 3;
   static final int CONTRACTION_ING_CONV = 4;
   static final int DOUBLED_CONSONANT_ING_CONV = 5;
   static final int SIMPLE_PLURAL_CONV = 6;
   static final int ES_PLURAL_CONV = 7;
   static final int IES_PLURAL_CONV = 8;
   static final int YS_PLURAL_CONV = 9;
   static final int NUMBER_ADJ_CONV = 10;

   EnglishConversionRules$Conversion(int conversionType) {
      this._conversionType = conversionType;
   }

   int getConversionType() {
      return this._conversionType;
   }

   public int applyConversion(char[] word, int offset, int len) {
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
