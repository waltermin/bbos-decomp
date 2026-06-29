package net.rim.tools.compiler.util;

import net.rim.tools.compiler.exec.CharacterHelper;

public final class StringHelper {
   public static final String trim(String str) {
      if (str == null) {
         return null;
      }

      int len = str.length();
      int start = 0;

      while (start < len && str.charAt(start) == ' ') {
         start++;
      }

      int end = len;

      while (end > start && str.charAt(end - 1) == ' ') {
         end--;
      }

      if (start > 0 || end < len) {
         str = str.substring(start, end);
      }

      return str;
   }

   public static final boolean validateIdentifier(String name) {
      int len = name.length();
      if (len == 0) {
         return false;
      }

      if (!CharacterHelper.isJavaIdentifierStart(name.charAt(0))) {
         return false;
      }

      for (int i = 1; i < len; i++) {
         if (!CharacterHelper.isJavaIdentifierPart(name.charAt(i))) {
            return false;
         }
      }

      return true;
   }
}
