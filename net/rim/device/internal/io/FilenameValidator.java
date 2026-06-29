package net.rim.device.internal.io;

public final class FilenameValidator {
   private FilenameValidator() {
   }

   public static final boolean validateFilenameAndPath(String name) {
      boolean valid = true;
      int length = name.length();

      for (int i = 0; valid && i < length; i++) {
         valid = validateChar(name.charAt(i), true);
      }

      return valid;
   }

   public static final boolean validateChar(char ch) {
      return validateChar(ch, false);
   }

   private static final boolean validateChar(char ch, boolean path) {
      switch (ch) {
         case '"':
         case '*':
         case ':':
         case '<':
         case '>':
         case '?':
         case '\\':
         case '|':
            return false;
         case '/':
            return path;
         default:
            return ch >= ' ';
      }
   }
}
