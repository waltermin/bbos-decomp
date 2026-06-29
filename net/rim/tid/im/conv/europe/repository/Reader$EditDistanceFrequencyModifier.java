package net.rim.tid.im.conv.europe.repository;

import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ResultContainer$FrequencyModifier;
import net.rim.tid.util.Utils;

class Reader$EditDistanceFrequencyModifier implements ResultContainer$FrequencyModifier {
   private char[] _userInput;
   private int _userInputLen;
   private final Reader this$0;
   private static final int CHAR_DIFF_SHIFT = 8;
   private static final int CASE_DIFF_SHIFT = 5;
   private static final int ACCENT_DIFF_SHIFT = 6;
   private static final int SHARED_KEY_SHIFT = 6;
   private static final int ADJACENT_KEY_SHIFT = 7;
   private static final int CHAR_DIFF_COST = 256;
   private static final int CASE_DIFF_COST = 32;
   private static final int ACCENT_DIFF_COST = 64;
   private static final int ADJACENT_KEY_COST = 128;
   private static final int SHARED_KEY_COST = 0;

   private Reader$EditDistanceFrequencyModifier(Reader _1) {
      this.this$0 = _1;
   }

   public void setUserInput(char[] input, int len) {
      if (this._userInput == null || this._userInput.length < len) {
         this._userInput = new char[len];
      }

      this._userInputLen = 0;
      boolean opened = false;

      for (int i = 0; i < len; i++) {
         switch (input[i]) {
            case '\u0000':
               this._userInput[this._userInputLen] = input[i];
               if (!opened) {
                  this._userInputLen++;
               }
            case '\u0001':
               break;
            case '\u0002':
            default:
               opened = true;
               break;
            case '\u0003':
               opened = false;
               this._userInputLen++;
         }
      }
   }

   @Override
   public void modifyFrequency(ExtendedCurrentVariant variant) {
      variant._frequency = 32767 - this.computeEditDistance(this._userInput, this._userInputLen, variant._variants, variant._offset, variant._length);
   }

   private short computeEditDistance(char[] w1, int len1, char[] w2, int offset2, int len2) {
      int rowLen = len1 + 1;
      int tableLen = rowLen * (len2 + 1);
      short[] dt = new short[tableLen];

      for (int i = 1; i <= len1; i++) {
         dt[i] = (short)(i << 8);
      }

      int row = rowLen;

      for (int i = 1; row < tableLen; i++) {
         dt[row] = (short)(i << 8);
         row += rowLen;
      }

      for (int j = offset2 + 1; j <= offset2 + len2; j++) {
         char ch2 = w2[j - 1];

         for (int i = 1; i <= len1; i++) {
            char ch1 = w1[i - 1];
            if (ch1 == 4) {
               dt[j * rowLen + i] = 100;
            } else {
               int cost;
               if (ch1 == ch2) {
                  cost = 0;
               } else {
                  char lc1 = Utils.toLowerCase(ch1);
                  char lc2 = Utils.toLowerCase(ch2);
                  if (lc1 == lc2) {
                     cost = 32;
                  } else if (KeypadLayout.isAdjacent(lc1, lc2)) {
                     cost = 128;
                  } else if (KeypadLayout.isShared(lc1, lc2)) {
                     cost = 0;
                  } else {
                     cost = 256;
                  }
               }

               int dist = dt[rowLen * (j - 1) + i] + 256;
               dist = Math.min(dist, dt[rowLen * j + i - 1] + 256);
               dist = Math.min(dist, dt[rowLen * (j - 1) + i - 1] + cost);
               if (j > 1 && i > 1 && w2[j - 2] == w1[i - 1] && w2[j - 1] == w1[i - 2]) {
                  int transCost = dt[rowLen * (j - 2) + i - 2] + cost;
                  if (transCost < dist) {
                     dist = transCost;
                  }
               }

               dt[j * rowLen + i] = (short)dist;
            }
         }
      }

      return dt[rowLen * len2 + len1];
   }

   Reader$EditDistanceFrequencyModifier(Reader x0, Reader$1 x1) {
      this(x0);
   }
}
