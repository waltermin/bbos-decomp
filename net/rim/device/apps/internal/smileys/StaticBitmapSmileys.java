package net.rim.device.apps.internal.smileys;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.StringPattern$Match;

public class StaticBitmapSmileys extends EmoticonStringPattern {
   private Bitmap _bitmap;
   private int _size;
   private int[][] _layouts = new int[0][];
   private String[] _text = new Object[0];
   private String[] _searchText = new Object[0];
   private int[] _searchIds = new int[0];
   static ReverseTextComparator _comparator = new ReverseTextComparator();
   private static short[] _skipJump;

   public void setImageData(byte[] imageData) {
      this._bitmap = Bitmap.createBitmapFromBytes(imageData, 0, -1, 1);
      this._size = this._bitmap.getHeight();
   }

   public void add(String text, int imageIndex) {
      if (this._text.length <= imageIndex) {
         Arrays.add(this._text, text);
      }

      text = text.toUpperCase();
      int index = Arrays.binarySearch(this._searchText, text, _comparator, 0, this._searchText.length);
      if (index < 0) {
         index = -index - 1;
         Arrays.insertAt(this._searchText, text, index);
         Arrays.insertAt(this._searchIds, imageIndex, index);
      }
   }

   public void addLayout(int[] layout) {
      Arrays.add(this._layouts, layout);
   }

   @Override
   public int emoticonSize() {
      return this._size;
   }

   @Override
   public void drawEmoticon(Graphics graphics, int id, int x, int y) {
      graphics.drawBitmap(x, y, this._size, this._size, this._bitmap, id * this._size, 0);
   }

   @Override
   public String emoticonReplacementText(int id) {
      return this._text[id];
   }

   @Override
   public String emoticonDescription(int id) {
      String[] descriptions = SmileysResources.getStringArray(6000);
      return descriptions != null && id < descriptions.length ? descriptions[id] : null;
   }

   @Override
   public int[][] emoticonScreenLayouts() {
      return this._layouts;
   }

   void buildSkipJump() {
      _skipJump = new short[128];

      for (int i = 0; i < 128; i++) {
         _skipJump[i] = 2;
      }

      int count = this._searchText.length;

      for (int i = 0; i < count; i++) {
         String text = this._searchText[i];
         _skipJump[text.charAt(text.length() - 2)] = 1;
      }

      char currentChar = 255;
      int low = 0;

      for (int i = 0; i < count; i++) {
         String text = this._searchText[i];
         int length = text.length();
         char thisChar = text.charAt(length - 1);
         if (thisChar != currentChar) {
            if (currentChar != 255) {
               _skipJump[currentChar] = (short)((i - 1 << 8) + low);
            }

            currentChar = thisChar;
            low = i;
         }
      }

      if (currentChar != 255) {
         _skipJump[currentChar] = (short)((count - 1 << 8) + low);
      }
   }

   @Override
   public boolean findMatch(AbstractString str, int start, int end, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      if (_skipJump == null) {
         this.buildSkipJump();
      }

      int i = start;
      boolean found = false;

      while (i < end) {
         char c = str.charAt(i);
         if (c >= 128) {
            i += 2;
         } else {
            short jump = _skipJump[Character.toUpperCase(str.charAt(i))];
            int low = jump & 255;
            int high = jump >> 8;
            if (low > high) {
               i += low;
            } else {
               while (low <= high) {
                  int mid = low + high >> 1;
                  String testStr = this._searchText[mid];
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
                        match.id = this._searchIds[mid];
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
