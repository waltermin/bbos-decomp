package net.rim.device.internal.ui;

import net.rim.device.api.util.AbstractString;

class StringBufferGap$SBGAbstractString implements AbstractString {
   private int _labelLength;
   private final StringBufferGap this$0;

   public void setLabelLength(int length) {
      this._labelLength = length;
   }

   @Override
   public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      this.this$0.getChars(this._labelLength + srcBegin, this._labelLength + srcEnd, dst, dstBegin);
   }

   @Override
   public int indexOf(char c, int startIndex, int endIndex) {
      int index = this.this$0.indexOf(c, this._labelLength + startIndex, this._labelLength + endIndex);
      return index >= this._labelLength ? index - this._labelLength : -1;
   }

   @Override
   public int length() {
      return this.this$0._size - this._labelLength;
   }

   @Override
   public char charAt(int index) {
      return this.this$0.charAt(this._labelLength + index);
   }

   StringBufferGap$SBGAbstractString(StringBufferGap _1) {
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return this.this$0.getText(this._labelLength, this.this$0._size - this._labelLength);
   }
}
