package net.rim.tools.compiler.vm;

import net.rim.vm.ClassInfo;

public final class IdEncode {
   static final int SIZE;
   static final char ESC;
   private static char[] _chars = new char[8];
   private static byte[] _bytes = new byte[8];

   public static final String encode(String id) {
      int len = id.length();
      if (len < 1) {
         return id;
      }

      char[] chars = _chars;
      if (len > chars.length) {
         _chars = new char[len];
         chars = _chars;
      }

      id.getChars(0, len, chars, 0);
      int escapeLength = 0;
      byte[] bytes = _bytes;
      if (len * 4 > bytes.length) {
         _bytes = new byte[len * 4];
         bytes = _bytes;
      }

      char[] cc2 = IdEncoding.codeChar2;
      int cc2len = cc2.length;
      int num = 0;

      for (int i = 0; i < len; i++) {
         char c = chars[i];
         if (c == 255 || c < 0 || c >= cc2len || cc2[c] != 0) {
            if (c > 255) {
               escapeLength++;
               bytes[num++] = -1;
               bytes[num++] = (byte)(c >> '\b' & 0xFF);
            }

            escapeLength++;
            bytes[num++] = -1;
         }

         bytes[num++] = (byte)(c & 0xFF);
      }

      if (num - escapeLength > 1) {
         num = ClassInfo.encodeBytes(bytes, num);
      }

      return (String)(new Object(bytes, 0, num));
   }
}
