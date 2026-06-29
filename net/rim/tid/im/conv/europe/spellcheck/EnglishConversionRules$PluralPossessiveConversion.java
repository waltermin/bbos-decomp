package net.rim.tid.im.conv.europe.spellcheck;

class EnglishConversionRules$PluralPossessiveConversion extends EnglishConversionRules$Conversion {
   EnglishConversionRules$Conversion _pluralConversion;
   boolean _invalidSuffixS;

   EnglishConversionRules$PluralPossessiveConversion(EnglishConversionRules$Conversion pluralConversion, boolean invalidSuffixS) {
      super(2);
      this._pluralConversion = pluralConversion;
      this._invalidSuffixS = invalidSuffixS;
   }

   EnglishConversionRules$PluralPossessiveConversion(EnglishConversionRules$Conversion pluralConversion) {
      this(pluralConversion, false);
   }

   @Override
   public int applyConversion(char[] word, int offset, int len) {
      return this._pluralConversion.applyConversion(word, offset, this._invalidSuffixS ? len - 2 : len - 1);
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      this._pluralConversion.setConversionBounds(word, offset, this._invalidSuffixS ? len - 2 : len - 1, bounds);
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return this._pluralConversion.isValidForReverseConversion(word, offset, len);
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      len = this._pluralConversion.reverseConversion(word, offset, len);
      int index = offset + len;
      EnglishConversionRules.ensureSize(word, offset + len + 1);
      word[index] = '\'';
      return len + 1;
   }
}
