package net.rim.device.api.util;

public class StringPattern {
   public boolean findMatch(AbstractString str, int beginIndex, StringPattern$Match match) {
      return str == null ? false : this.findMatch(str, beginIndex, str.length(), match);
   }

   public boolean findMatch(AbstractString _1, int _2, int _3, StringPattern$Match _4) {
      throw null;
   }

   public boolean findMatch(AbstractString str, int beginIndex, int curIndex, int maxIndex, StringPattern$Match match) {
      return false;
   }

   public long getPatternTypeIdentifier() {
      return -1;
   }

   protected static final boolean isWhitespace(char c) {
      switch (c) {
         case '\t':
         case '\n':
         case '\r':
         case ' ':
         case ' ':
         case '\u200b':
            return true;
         default:
            return false;
      }
   }
}
