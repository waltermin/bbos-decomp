package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.ui.StringBufferGap;

public class SymbolFilter implements ITextFilter {
   private static String SYMBOLS = "@#$%^&*_+";

   @Override
   public int filter(StringBufferGap aText, int aStart, int aEnd, boolean aBackwards) {
      int start = aStart;
      if (aBackwards) {
         while (start > 0) {
            char ch = aText.charAt(start - 1);
            if (CharacterUtilities.isSpaceChar(ch)) {
               break;
            }

            start--;
         }
      }

      int len = aText.length();
      int word_len = 0;
      boolean is_mixed_word = false;

      for (int index = start; index < len; index++) {
         char ch = aText.charAt(index);
         if (SYMBOLS.indexOf(ch) != -1) {
            is_mixed_word = true;
         } else if (CharacterUtilities.isSpaceChar(ch) || CharacterUtilities.isPunctuation(ch)) {
            break;
         }

         word_len++;
      }

      int end = start + word_len;
      if (is_mixed_word) {
         return aBackwards ? start - aStart : end - aStart;
      } else {
         return 0;
      }
   }

   @Override
   public void reset() {
   }
}
