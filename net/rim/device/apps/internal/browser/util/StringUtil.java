package net.rim.device.apps.internal.browser.util;

public final class StringUtil {
   public static final int stringBufferIndexOf(StringBuffer sb, String str) {
      int strLength = str.length();
      if (strLength == 0) {
         return 0;
      }

      char str_c0 = str.charAt(0);
      int sbLength = sb.length();
      int sbLimit = sbLength - strLength;

      label31:
      for (int i = 0; i <= sbLimit; i++) {
         if (sb.charAt(i) == str_c0) {
            for (int j = 1; j < strLength; j++) {
               if (sb.charAt(i + j) != str.charAt(j)) {
                  continue label31;
               }
            }

            return i;
         }
      }

      return -1;
   }

   public static final int stringBufferIndexOf(StringBuffer sb, int startIndex, char c) {
      int sbLength = sb.length();

      for (int i = startIndex; i < sbLength; i++) {
         if (sb.charAt(i) == c) {
            return i;
         }
      }

      return -1;
   }
}
