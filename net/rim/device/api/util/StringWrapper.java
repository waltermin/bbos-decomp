package net.rim.device.api.util;

final class StringWrapper extends AbstractStringWrapper {
   private String _string;

   public StringWrapper(String str) {
      this._string = str;
   }

   @Override
   public final String toString() {
      return this._string.toString();
   }

   @Override
   public final int hashCode() {
      return this._string.hashCode();
   }

   @Override
   public final int length() {
      return this._string.length();
   }

   @Override
   public final void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      this._string.getChars(srcBegin, srcEnd, dst, dstBegin);
   }

   @Override
   public final int indexOf(char c, int beginIndex, int endIndex) {
      return StringUtilities.indexOf(this._string, c, beginIndex, endIndex);
   }

   @Override
   public final char charAt(int index) {
      return this._string.charAt(index);
   }

   @Override
   public final void reset(Object string) {
      if (!(string instanceof String)) {
         throw new IllegalArgumentException();
      }

      String str = (String)string;
      this._string = str;
   }
}
