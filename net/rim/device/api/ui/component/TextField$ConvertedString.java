package net.rim.device.api.ui.component;

import net.rim.device.api.util.AbstractString;

final class TextField$ConvertedString implements AbstractString {
   private char[] _insertedFilteredChars;
   private int _insertPos;
   private int _filteredCharsCount;
   private final TextField this$0;

   private TextField$ConvertedString(TextField _1) {
      this.this$0 = _1;
   }

   final void init(int insertedLen, int insertPos) {
      if (this._insertedFilteredChars == null || this._insertedFilteredChars.length < insertedLen) {
         this._insertedFilteredChars = new char[insertedLen];
      }

      this._filteredCharsCount = 0;
      this._insertPos = insertPos;
   }

   final void appendFilteredChar(char ch) {
      this._insertedFilteredChars[this._filteredCharsCount++] = ch;
   }

   final String getConvertedInsertionString() {
      return new String(this._insertedFilteredChars, 0, this._filteredCharsCount);
   }

   @Override
   public final int length() {
      return this.this$0.getTextLength() + this._filteredCharsCount;
   }

   @Override
   public final int indexOf(char c, int startIndex, int endIndex) {
      for (int i = startIndex; i < endIndex; i++) {
         if (this.charAt(i) == c) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final char charAt(int index) {
      index += this.this$0.getLabelLength();
      if (index < this._insertPos) {
         return this.this$0._text.charAt(index);
      } else {
         return index < this._insertPos + this._filteredCharsCount
            ? this._insertedFilteredChars[index - this._insertPos]
            : this.this$0._text.charAt(index - this._filteredCharsCount);
      }
   }

   @Override
   public final void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      int i = srcBegin;

      for (int j = dstBegin; i < srcEnd; j++) {
         dst[j] = this.charAt(i);
         i++;
      }
   }

   TextField$ConvertedString(TextField x0, TextField$1 x1) {
      this(x0);
   }
}
