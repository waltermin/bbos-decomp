package net.rim.tools.compiler.exec;

import net.rim.vm.Memory;

public class CharacterHelper {
   private static char[] _chars = new char[8];
   private static char[] _charLock = _chars;

   public static boolean isJavaIdentifierStart(char ch) {
      if ('a' <= ch && ch <= 'z') {
         return true;
      } else if ('A' <= ch && ch <= 'Z') {
         return true;
      } else if (ch == '_') {
         return true;
      } else {
         return ch == '$' ? true : ch > 127;
      }
   }

   public static boolean isJavaIdentifierPart(char ch) {
      if ('a' <= ch && ch <= 'z') {
         return true;
      } else if ('A' <= ch && ch <= 'Z') {
         return true;
      } else if ('0' <= ch && ch <= '9') {
         return true;
      } else if (ch == '_') {
         return true;
      } else {
         return ch == '$' ? true : ch > 127;
      }
   }

   public static String intern(String s) {
      return Memory.stringIntern(s);
   }

   public static String utf8ToString(byte[] bytes) {
      return utf8ToString(bytes, 0, bytes.length);
   }

   public static String utf8ToString(byte[] bytes, int offset, int length) {
      String string = null;
      int suffixChars = 0;
      int end = offset + length;

      for (int i = offset; i < end; i++) {
         char ch1 = (char)bytes[i];
         if (ch1 == 0) {
            throw new Object("invalid UTF-8 encoding");
         }

         if ((ch1 & 128) != 0) {
            suffixChars++;
            char ch2 = (char)bytes[++i];
            if ((ch2 & 192) != 128) {
               throw new Object("invalid UTF-8 encoding");
            }

            if ((ch1 & 224) != 192) {
               if ((ch1 & 240) != 224) {
                  throw new Object("invalid UTF-8 encoding");
               }

               suffixChars++;
               char ch3 = (char)bytes[++i];
               if ((ch3 & 192) != 128) {
                  throw new Object("invalid UTF-8 encoding");
               }
            }
         }
      }

      if (suffixChars == 0) {
         string = (String)(new Object(bytes, offset, length));
      } else {
         synchronized (_charLock) {
            char[] chars = _chars;
            if (chars.length < length - suffixChars) {
               chars = MyArrays.resize(chars, length - suffixChars);
               _chars = chars;
            }

            int strIndex = 0;

            for (int var16 = offset; var16 < end; var16++) {
               char ch1 = (char)bytes[var16];
               if ((ch1 & 128) == 0) {
                  chars[strIndex++] = ch1;
               } else if ((ch1 & 224) == 192) {
                  char ch2 = (char)bytes[++var16];
                  chars[strIndex++] = (char)((ch1 & 31) << 6 | ch2 & 63);
               } else {
                  char ch2 = (char)bytes[++var16];
                  char ch3 = (char)bytes[++var16];
                  chars[strIndex++] = (char)((ch1 & 15) << 12 | (ch2 & '?') << 6 | ch3 & 63);
               }
            }

            string = (String)(new Object(chars, 0, strIndex));
         }
      }

      return intern(string);
   }
}
