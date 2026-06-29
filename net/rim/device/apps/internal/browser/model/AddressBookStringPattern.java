package net.rim.device.apps.internal.browser.model;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public final class AddressBookStringPattern extends StringPattern {
   private WeakReference _scanbufferWR = new WeakReference(null);
   private static final int SCAN_BUFFER_SIZE = 64;
   private static final int STATE_INIT = 0;
   private static final int STATE_W = 10;
   private static final int STATE_WT = 11;
   private static final int STATE_WTA = 12;
   private static final int STATE_WTAI = 13;
   private static final int STATE_WTAI_ = 14;
   private static final int STATE_WTAI__ = 15;
   private static final int STATE_WTAI___ = 16;
   private static final int STATE_WTAI___W = 17;
   private static final int STATE_WTAI___WP = 18;
   private static final int STATE_WTAI___WP_ = 19;
   private static final int STATE_WTAI___WP_A = 20;
   private static final int STATE_WTAI___WP_AP = 21;
   private static final int STATE_WTAI___WP_AP_ = 22;

   @Override
   public final long getPatternTypeIdentifier() {
      return 4085495887538053543L;
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
         char ch = str.charAt(beginIndex);
         switch (ch) {
            case 'W':
            case 'w':
               match.prefixLength = 13;
               match.id = 4085495887538053543L;
            default:
               if (isValidBeginIndex(str, beginIndex)) {
                  match.beginIndex = beginIndex;
                  match.endIndex = getEndIndex(str, beginIndex + match.prefixLength - 1, maxIndex);
                  if (match.endIndex - match.beginIndex > match.prefixLength) {
                     return true;
                  }
               }

               index = beginIndex + match.prefixLength + 1;
         }
      }

      return false;
   }

   private final int getNextIndex(AbstractString str, int index, int maxIndex) {
      int state = 0;
      char[] scanbuffer = WeakReferenceUtilities.getCharArray(this._scanbufferWR, 64);

      while (index < maxIndex) {
         if (state == 0) {
            int colonIndex = str.indexOf(':', index, maxIndex);
            if (colonIndex < index || colonIndex >= maxIndex) {
               return maxIndex;
            }

            if (colonIndex - 4 > index) {
               index = colonIndex - 4;
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
               case 22:
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
               case 14:
                  return 15;
               case 15:
                  return 16;
               case 18:
                  return 19;
               default:
                  return 0;
            }
         case ':':
            switch (state) {
               case 13:
                  return 14;
               default:
                  return 0;
            }
         case ';':
            if (state == 21) {
               return 22;
            }

            return 0;
         case 'A':
         case 'a':
            switch (state) {
               case 11:
                  return 12;
               case 19:
                  return 20;
               default:
                  return 0;
            }
         case 'I':
         case 'i':
            if (state == 12) {
               return 13;
            }

            return 0;
         case 'P':
         case 'p':
            switch (state) {
               case 17:
                  return 18;
               case 20:
                  return 21;
               default:
                  return 0;
            }
         case 'T':
         case 't':
            switch (state) {
               case 10:
                  return 11;
               default:
                  return 0;
            }
         case 'W':
         case 'w':
            switch (state) {
               case 0:
                  return 10;
               case 16:
                  return 17;
               default:
                  return 0;
            }
         default:
            return 0;
      }
   }

   static final int getPrefixLength(AbstractString str) {
      int state = 0;
      int len = str.length();

      for (int idx = 0; idx < len; idx++) {
         state = getNextState(state, str.charAt(idx));
         switch (state) {
            case 0:
               return 0;
            case 22:
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
