package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;

public final class PINAddressStringPattern extends StringPattern {
   @Override
   public final long getPatternTypeIdentifier() {
      return 4246852237058296601L;
   }

   @Override
   public final synchronized boolean findMatch(AbstractString str, int index, int maxIndex, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      while (index < maxIndex) {
         int beginIndex = this.getNextIndex(str, index, maxIndex);
         if (beginIndex < index) {
            break;
         }

         if (beginIndex >= maxIndex) {
            return false;
         }

         match.prefixLength = 4;
         match.id = 4246852237058296601L;
         if (isValidBeginIndex(str, beginIndex)) {
            match.beginIndex = beginIndex;
            match.endIndex = getEndIndex(str, beginIndex + match.prefixLength - 1, maxIndex);
            if (match.endIndex - match.beginIndex > match.prefixLength) {
               return true;
            }
         }

         index = beginIndex + match.prefixLength + 1;
      }

      return false;
   }

   private final int getNextIndex(AbstractString str, int index, int maxIndex) {
      while (index < maxIndex) {
         int colonIndex = str.indexOf(':', index, maxIndex);
         if (colonIndex < index || colonIndex >= maxIndex) {
            return maxIndex;
         }

         if (colonIndex - 3 >= index) {
            index = colonIndex - 1;
            char c = str.charAt(index);
            if (c == 'n' || c == 'N') {
               c = str.charAt(--index);
               if (c == 'i' || c == 'I') {
                  c = str.charAt(--index);
                  if (c == 'p' || c == 'P') {
                     return index;
                  }
               }
            }
         }

         index = colonIndex + 1;
      }

      return index;
   }

   private static final boolean isValidBeginIndex(AbstractString str, int beginIndex) {
      while (beginIndex > 0) {
         char c = str.charAt(beginIndex - 1);
         if (c == '|') {
            return true;
         }

         if (!isPunctuation(c)) {
            if (StringPattern.isWhitespace(c)) {
               return true;
            }

            return false;
         }

         beginIndex--;
      }

      return true;
   }

   private static final int getEndIndex(AbstractString str, int beginIndex, int maxIndex) {
      int endIndex = beginIndex + 1;
      if (endIndex < maxIndex && str.charAt(endIndex) == ' ') {
         endIndex++;
      }

      int digitCount = 0;
      boolean hasHex = false;

      label38:
      while (endIndex < maxIndex) {
         char c = str.charAt(endIndex);
         switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               digitCount++;
               break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
               digitCount++;
               hasHex = true;
               break;
            default:
               if (!isPunctuation(c) && !StringPattern.isWhitespace(c)) {
                  return -1;
               }
               break label38;
         }

         endIndex++;
      }

      if (!hasHex && digitCount == 7) {
         return endIndex;
      } else {
         return digitCount == 8 ? endIndex : -1;
      }
   }

   private static final boolean isPunctuation(char c) {
      switch (c) {
         case '!':
         case '"':
         case '\'':
         case '(':
         case ')':
         case ',':
         case '.':
         case ':':
         case ';':
         case '<':
         case '>':
         case '?':
         case '[':
         case ']':
         case '{':
         case '}':
            return true;
         default:
            return false;
      }
   }
}
