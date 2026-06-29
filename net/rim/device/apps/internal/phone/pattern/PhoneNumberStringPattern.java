package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public final class PhoneNumberStringPattern extends StringPattern {
   private WeakReference _scanbufferWR = (WeakReference)(new Object(null));
   private static final int SCAN_BUFFER_SIZE = 64;
   private static final int STATE_INIT = 0;
   private static final int STATE_F = 10;
   private static final int STATE_FA = 11;
   private static final int STATE_FAX = 12;
   private static final int STATE_FAX_ = 13;
   private static final int STATE_T = 20;
   private static final int STATE_TE = 21;
   private static final int STATE_TEL = 22;
   private static final int STATE_TEL_ = 23;
   private static final int STATE_W = 30;
   private static final int STATE_WT = 31;
   private static final int STATE_WTA = 32;
   private static final int STATE_WTAI = 33;
   private static final int STATE_WTAI_ = 34;
   private static final int STATE_WTAI__ = 35;
   private static final int STATE_WTAI___ = 36;
   private static final int STATE_WTAI___W = 37;
   private static final int STATE_WTAI___WP = 38;
   private static final int STATE_WTAI___WP_ = 39;
   private static final int STATE_WTAI___WP_M = 40;
   private static final int STATE_WTAI___WP_MC = 41;
   private static final int STATE_WTAI___WP_MC_ = 42;
   private static final int STATE_C = 50;
   private static final int STATE_CT = 51;
   private static final int STATE_CTI = 52;
   private static final int STATE_CTI_ = 53;

   @Override
   public final long getPatternTypeIdentifier() {
      return 3797587162219887872L;
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

         match.prefixLength = maxIndex;
         switch (str.charAt(beginIndex)) {
            case 'C':
            case 'c':
               match.prefixLength = 4;
               match.id = 3797587162219887872L;
               break;
            case 'F':
            case 'f':
               match.prefixLength = 4;
               match.id = 2862138288634470671L;
               break;
            case 'T':
            case 't':
               match.prefixLength = 4;
               match.id = 3797587162219887872L;
               break;
            case 'W':
            case 'w':
               match.prefixLength = 13;
               match.id = 3797587162219887872L;
         }

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
      int state = 0;
      char[] scanbuffer = WeakReferenceUtilities.getCharArray(this._scanbufferWR, 64);

      while (index < maxIndex) {
         if (state == 0) {
            int colonIndex = str.indexOf(':', index, maxIndex);
            if (colonIndex < index + 3 || colonIndex >= maxIndex) {
               return maxIndex;
            }

            if (colonIndex - 4 > index) {
               index = colonIndex - 4;
            }

            switch (str.charAt(colonIndex - 1)) {
               case 'I':
               case 'L':
               case 'X':
               case 'i':
               case 'l':
               case 'x':
                  break;
               default:
                  index = colonIndex + 1;
                  continue;
            }
         }

         int count = maxIndex - index;
         if (count > scanbuffer.length) {
            count = scanbuffer.length;
         }

         str.getChars(index, index + count, scanbuffer, 0);

         for (int bufIndex = 0; bufIndex < count; index++) {
            state = getNextState(state, scanbuffer[bufIndex]);
            switch (state) {
               case 13:
               case 23:
               case 53:
                  return index - 3;
               case 42:
                  return index - 12;
               default:
                  bufIndex++;
            }
         }
      }

      return index;
   }

   private static final int getNextState(int state, char c) {
      switch (c) {
         case '/':
            switch (state) {
               case 34:
                  return 35;
               case 35:
                  return 36;
               case 38:
                  return 39;
               default:
                  return 0;
            }
         case ':':
            switch (state) {
               case 12:
                  return 13;
               case 22:
                  return 23;
               case 33:
                  return 34;
               case 52:
                  return 53;
               default:
                  return 0;
            }
         case ';':
            if (state == 41) {
               return 42;
            }

            return 0;
         case 'A':
         case 'a':
            switch (state) {
               case 10:
                  return 11;
               case 31:
                  return 32;
               default:
                  return 0;
            }
         case 'C':
         case 'c':
            switch (state) {
               case 0:
                  return 50;
               case 40:
                  return 41;
               default:
                  return 0;
            }
         case 'E':
         case 'e':
            if (state == 20) {
               return 21;
            }

            return 0;
         case 'F':
         case 'f':
            if (state == 0) {
               return 10;
            }
            break;
         case 'I':
         case 'i':
            switch (state) {
               case 32:
                  return 33;
               case 51:
                  return 52;
               default:
                  return 0;
            }
         case 'L':
         case 'l':
            if (state == 21) {
               return 22;
            }

            return 0;
         case 'M':
         case 'm':
            if (state == 39) {
               return 40;
            }

            return 0;
         case 'P':
         case 'p':
            if (state == 37) {
               return 38;
            }

            return 0;
         case 'T':
         case 't':
            switch (state) {
               case 0:
                  return 20;
               case 30:
                  return 31;
               case 50:
                  return 51;
               default:
                  return 0;
            }
         case 'W':
         case 'w':
            switch (state) {
               case 0:
                  return 30;
               case 36:
                  return 37;
               default:
                  return 0;
            }
         case 'X':
         case 'x':
            if (state == 11) {
               return 12;
            }

            return 0;
         default:
            state = 0;
      }

      return state;
   }

   public static final int getPrefixLength(AbstractString str) {
      int state = 0;
      int len = str.length();

      for (int idx = 0; idx < len; idx++) {
         state = getNextState(state, str.charAt(idx));
         switch (state) {
            case 0:
               return 0;
            case 13:
            case 23:
            case 42:
            case 53:
               return idx + 1;
         }
      }

      return 0;
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

      while (endIndex < maxIndex && !StringPattern.isWhitespace(str.charAt(endIndex))) {
         endIndex++;
      }

      while (endIndex > beginIndex && isPunctuation(str.charAt(endIndex - 1))) {
         endIndex--;
      }

      return endIndex;
   }

   static final boolean isPunctuation(char c) {
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
         case '|':
         case '}':
            return true;
         default:
            return false;
      }
   }
}
