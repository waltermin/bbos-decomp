package net.rim.tid.im.conv.europe;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.text.AttributedString;
import net.rim.tid.util.Utils;

public class SLEuropeanWordsCorrector {
   public boolean applyCorrection(StringBuffer uncommited, StringBuffer committed, boolean isStartPos, boolean isEnglish, boolean autotextOn) {
      if (isEnglish && this.checkEnglishI(uncommited, autotextOn)) {
         return !autotextOn;
      } else if (isCompleteSentence(committed, isStartPos) && !CharacterUtilities.isUpperCase(uncommited.charAt(0))) {
         uncommited.setCharAt(0, Utils.toUpperCase(uncommited.charAt(0)));
         return true;
      } else {
         return false;
      }
   }

   public static boolean isCompleteSentence(StringBuffer aText, boolean aIsStartPos) {
      if (aText != null && aText.length() != 0) {
         int len = aText.length();
         int index = len - 1;
         if (len > 0 && !isWhitespace(aText.charAt(index))) {
            return false;
         }

         char ch = 0;
         boolean isWhiteChar = true;

         while (index >= 0) {
            ch = aText.charAt(index--);
            isWhiteChar = isWhitespace(ch);
            if (!isWhiteChar) {
               break;
            }
         }

         return isEndPunctuation(ch);
      } else {
         return aIsStartPos;
      }
   }

   public boolean applyCorrection(
      StringBuffer uncommited, AttributedString committed, int startPos, int endPos, boolean isStartPos, boolean isEnglish, boolean autotextOn
   ) {
      if (isEnglish && this.checkEnglishI(uncommited, autotextOn)) {
         return !autotextOn;
      } else if (isCompleteSentence(committed, startPos, endPos, isStartPos) && !CharacterUtilities.isUpperCase(uncommited.charAt(0))) {
         uncommited.setCharAt(0, Utils.toUpperCase(uncommited.charAt(0)));
         return true;
      } else {
         return false;
      }
   }

   public static boolean isCompleteSentence(AttributedString aText, int startPos, int endPos, boolean aIsStartPos) {
      if (aText != null && endPos - startPos != 0) {
         int index = endPos - 1;
         char ch = aText.charAt(index);
         if (!isWhitespace(ch)) {
            return false;
         }

         boolean isWhiteChar = true;

         while (!isEndPunctuation(ch) && index >= startPos) {
            ch = aText.charAt(index--);
            isWhiteChar = isWhitespace(ch);
            if (!isWhiteChar) {
               return isEndPunctuation(ch);
            }
         }

         return true;
      } else {
         return aIsStartPos;
      }
   }

   public static boolean isEndPunctuation(char aChar) {
      return ".!?\n\r".indexOf(aChar) != -1;
   }

   private boolean checkEnglishI(StringBuffer uncommited, boolean autotextOn) {
      int len = uncommited.length();
      if (len < 3) {
         if (uncommited.charAt(0) != 'i') {
            return false;
         }

         if (len == 2 && !isWhitespace(uncommited.charAt(1))) {
            return false;
         }

         if (!autotextOn) {
            uncommited.setCharAt(0, 'I');
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isWhitespace(char ch) {
      return " \t\n\r'".indexOf(ch) != -1;
   }
}
