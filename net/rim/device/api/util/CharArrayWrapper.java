package net.rim.device.api.util;

final class CharArrayWrapper extends AbstractStringWrapper {
   private char[] _charArray;

   public CharArrayWrapper(char[] charArray) {
      this._charArray = charArray;
   }

   @Override
   public final String toString() {
      return new String(this._charArray);
   }

   @Override
   public final int length() {
      return this._charArray.length;
   }

   @Override
   public final void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      System.arraycopy(this._charArray, srcBegin, dst, dstBegin, srcEnd - srcBegin);
   }

   @Override
   public final int indexOf(char c, int beginIndex, int endIndex) {
      for (int index = beginIndex; index < endIndex; index++) {
         if (this._charArray[index] == c) {
            return index;
         }
      }

      return -1;
   }

   @Override
   public final char charAt(int index) {
      return this._charArray[index];
   }

   @Override
   public final void reset(Object string) {
      if (!(string instanceof char[])) {
         throw new IllegalArgumentException();
      }

      char[] str = (char[])string;
      this._charArray = str;
   }
}
