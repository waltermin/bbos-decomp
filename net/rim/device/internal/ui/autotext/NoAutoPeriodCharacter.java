package net.rim.device.internal.ui.autotext;

final class NoAutoPeriodCharacter {
   private static String NO_AUTO_PERIOD_CHARS = ".*/+-=:@_$;?!,({[<\\&^|~`\n\r";
   private static String NO_AUTO_PERIOUD_EXTENDED_CHARS = ";。．｡";
   private static int[] _bitmap = new int[8];

   static final boolean isNoAutoPeriodCharacter(char c) {
      return c < 255 ? (_bitmap[c >> 5] & 1 << (c & 31)) != 0 : NO_AUTO_PERIOUD_EXTENDED_CHARS.indexOf(c) != -1;
   }

   static final String getNoAutoPeriodCharacterString() {
      return NO_AUTO_PERIOD_CHARS;
   }

   static {
      int numAutoPeriodOffChars = NO_AUTO_PERIOD_CHARS.length();

      for (int i = 0; i < numAutoPeriodOffChars; i++) {
         char autoPeriodOffChar = NO_AUTO_PERIOD_CHARS.charAt(i);
         _bitmap[autoPeriodOffChar >> 5] = _bitmap[autoPeriodOffChar >> 5] | 1 << (autoPeriodOffChar & 31);
      }
   }
}
