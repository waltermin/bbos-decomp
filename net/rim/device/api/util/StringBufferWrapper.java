package net.rim.device.api.util;

final class StringBufferWrapper extends AbstractStringWrapper {
   private StringBuffer _stringBuffer;
   private char[] _buffer = new char[64];

   public StringBufferWrapper(StringBuffer str) {
      this._stringBuffer = str;
   }

   @Override
   public final String toString() {
      return this._stringBuffer.toString();
   }

   @Override
   public final int length() {
      return this._stringBuffer.length();
   }

   @Override
   public final char charAt(int index) {
      return this._stringBuffer.charAt(index);
   }

   @Override
   public final void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      this._stringBuffer.getChars(srcBegin, srcEnd, dst, dstBegin);
   }

   @Override
   public final int indexOf(char c, int beginIndex, int endIndex) {
      for (int index = beginIndex; index < endIndex; index += 64) {
         int count = endIndex - index;
         if (count > 64) {
            count = 64;
         }

         this._stringBuffer.getChars(index, index + count, this._buffer, 0);

         for (int idx = 0; idx < count; idx++) {
            if (this._buffer[idx] == c) {
               return index + idx;
            }
         }
      }

      return -1;
   }

   @Override
   public final void reset(Object string) {
      if (!(string instanceof StringBuffer)) {
         throw new IllegalArgumentException();
      }

      StringBuffer str = (StringBuffer)string;
      this._stringBuffer = str;
   }
}
