package net.rim.tid.im.conv.europe.spellcheck;

class ItalianConversionRules$ContractionConversion extends ItalianConversionRules$Conversion {
   private StringBuffer _contractionPrefix = (StringBuffer)(new Object());

   ItalianConversionRules$ContractionConversion() {
      super(1);
   }

   void setContractionPrefix(char[] contractionPrefix, int offset, int len) {
      this._contractionPrefix.setLength(0);
      this._contractionPrefix.append(contractionPrefix, offset, len);
   }

   @Override
   public int applyConversion(char[] word, int len) {
      int plen = this._contractionPrefix.length();
      System.arraycopy(word, plen + 1, word, 0, len - plen - 1);
      return len - plen - 1;
   }

   @Override
   public void setConversionBounds(char[] word, int offset, int len, int[] bounds) {
      int plen = this._contractionPrefix.length();
      bounds[0] = offset + plen + 1;
      bounds[1] = len - plen - 1;
   }

   @Override
   public boolean isValidForReverseConversion(char[] word, int offset, int len) {
      return "aeiouh".indexOf(word[offset]) != -1;
   }

   @Override
   public int reverseConversion(char[] word, int offset, int len) {
      int plen = this._contractionPrefix.length();
      System.arraycopy(word, offset, word, offset + plen + 1, len);

      for (int i = 0; i < plen; i++) {
         word[offset + i] = this._contractionPrefix.charAt(i);
      }

      word[plen] = '\'';
      return len + plen + 1;
   }
}
