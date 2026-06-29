package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringPattern$Match;

public final class StringSetSearch {
   private String[] _set = new Object[0];
   private long[] _keys = new long[0];
   private short[] _skipJump;
   private int _shortestLength = 255;
   static ReverseTextComparator _comparator = new ReverseTextComparator();

   public final void add(String text, long key) {
      if (text.length() < 2) {
         throw new Object();
      }

      this._shortestLength = Math.min(this._shortestLength, text.length());
      text = text.toUpperCase();
      int index = Arrays.binarySearch(this._set, text, _comparator, 0, this._set.length);
      if (index < 0) {
         index = -index - 1;
         Arrays.insertAt(this._set, text, index);
         Arrays.insertAt(this._keys, key, index);
         this._skipJump = null;
      }
   }

   public final void buildSkipJump() {
      this._skipJump = new short[128];

      for (int i = 0; i < 128; i++) {
         this._skipJump[i] = (short)this._shortestLength;
      }

      int count = this._set.length;

      for (int i = 0; i < count; i++) {
         String text = this._set[i];
         int skip = 1;

         for (int iChar = text.length() - 2; iChar >= 0; iChar--) {
            char ch = text.charAt(iChar);
            if (skip < this._skipJump[ch]) {
               this._skipJump[ch] = (short)skip;
               this._skipJump[Character.toLowerCase(ch)] = (short)skip;
            }

            skip++;
         }
      }

      char currentChar = 255;
      int low = 0;

      for (int i = 0; i < count; i++) {
         String text = this._set[i];
         int length = text.length();
         char thisChar = text.charAt(length - 1);
         if (thisChar != currentChar) {
            if (currentChar != 255) {
               this._skipJump[currentChar] = (short)((i - 1 << 8) + low + 1);
            }

            currentChar = thisChar;
            low = i;
         }
      }

      if (currentChar != 255) {
         this._skipJump[currentChar] = (short)((count - 1 << 8) + low + 1);
      }
   }

   public final boolean findMatch(AbstractString str, int start, int end, StringPattern$Match match) {
      boolean found = false;
      int i = start;

      while (i < end) {
         int c = str.charAt(i);
         if (c >= 128) {
            i += this._shortestLength;
         } else {
            int jump = this._skipJump[c];
            if (jump < 256) {
               i += jump;
            } else {
               int low = (jump & 0xFF) - 1;
               int high = jump >> 8;

               while (low <= high) {
                  int mid = low + high >> 1;
                  String testStr = this._set[mid];
                  int length = testStr.length();
                  int testPos = length - 2;
                  int keyPos = i - 1;
                  int max = Math.min(length, i + 1);

                  int endPos;
                  for (endPos = i - max; keyPos > endPos; testPos--) {
                     int diff = Character.toUpperCase(str.charAt(keyPos)) - testStr.charAt(testPos);
                     if (diff > 0) {
                        low = mid + 1;
                        break;
                     }

                     if (diff < 0) {
                        high = mid - 1;
                        break;
                     }

                     keyPos--;
                  }

                  if (keyPos == endPos) {
                     if (length != max) {
                        high = mid - 1;
                     } else {
                        match.beginIndex = keyPos + 1;
                        match.endIndex = i + 1;
                        match.id = this._keys[mid];
                        low = mid + 1;
                        found = true;
                     }
                  }
               }

               if (found) {
                  return true;
               }

               i++;
            }
         }
      }

      return false;
   }
}
