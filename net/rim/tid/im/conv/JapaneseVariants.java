package net.rim.tid.im.conv;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class JapaneseVariants extends SLVariants {
   private StringBuffer _romaji = new StringBuffer();

   public JapaneseVariants(int hCount, int vCount) {
      super(vCount, false);
   }

   @Override
   public void persistentContentStateChanged(int state) {
      super.persistentContentStateChanged(state);
      if (state == 2) {
         this._romaji.setLength(0);
         this._romaji.setLength(this._romaji.capacity());
         this._romaji.setLength(0);
      }
   }

   public int getRomajiLength() {
      return this._romaji.length();
   }

   @Override
   public void clear() {
      super.clear();
      this._romaji.setLength(0);
   }

   public void clearVariants() {
      this.clearVariants(true);
   }

   public void clearVariants(boolean toUpdate) {
      super._variants._count = 0;
      super._additionalVariants._count = 0;
      super._variants.resetIteration();
      super._additionalVariants.resetIteration();
      super._isVariantsSeparated = false;
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void setOriginalAndRomajiLength(int length) {
      this._romaji.setLength(length * 3);
      this.getOriginal().setLength(length);
   }

   public int getRomajiString(StringBuffer data, int dataOffset, int romajiFirst, int romajiLast) {
      int len = this._romaji.length();
      if (romajiLast < 0) {
         romajiLast = len;
      } else {
         romajiLast *= 3;
      }

      if (romajiLast > len) {
         romajiLast = len;
      }

      int counter = 0;
      romajiFirst *= 3;

      for (int i = romajiFirst; i < romajiLast; i++) {
         char ch = this._romaji.charAt(i);
         if (ch >= ' ') {
            data.insert(dataOffset++, ch);
            counter++;
         }
      }

      return counter;
   }

   public int getRomajiString(char[] data, int dataOffset, int romajiFirst, int romajiLast) {
      int len = this._romaji.length();
      int counter = data.length - dataOffset;
      if (counter < 1) {
         return 0;
      }

      if (romajiLast < 0) {
         romajiLast = len;
      } else {
         romajiLast *= 3;
      }

      if (romajiLast > len) {
         romajiLast = len;
      }

      romajiFirst *= 3;
      if (romajiLast - romajiFirst > counter) {
         romajiLast = romajiFirst + counter;
      }

      counter = 0;

      for (int i = romajiFirst; i < romajiLast; i++) {
         char ch = this._romaji.charAt(i);
         if (ch >= ' ') {
            data[dataOffset++] = ch;
            counter++;
         }
      }

      return counter;
   }

   public int getNewCaretPos(boolean isForward, int aPosition) {
      if (aPosition < 0) {
         aPosition = super._caretPosition;
      }

      int romajiCaret = aPosition * 3;
      int len = this.getOriginal().length();
      int lenRomaji = this._romaji.length();
      if (isForward) {
         while (aPosition < len) {
            aPosition++;
            romajiCaret += 3;
            if (romajiCaret >= lenRomaji) {
               break;
            }

            if (this._romaji.charAt(romajiCaret) >= ' ') {
               return aPosition;
            }
         }
      } else {
         while (aPosition > 0) {
            aPosition--;
            romajiCaret -= 3;
            if (romajiCaret <= 0 || this._romaji.charAt(romajiCaret) >= ' ') {
               break;
            }
         }
      }

      return aPosition;
   }

   @Override
   public void moveCaret(boolean isForward) {
      super._caretPosition = this.getNewCaretPos(isForward, -1);
   }

   public void insertRomajis(char[] str, int aPosition) {
      if (aPosition == -1) {
         aPosition = super._caretPosition;
      }

      aPosition *= 3;
      if (aPosition >= 0 && aPosition <= this._romaji.length()) {
         this._romaji.insert(aPosition, str);
      }
   }

   public void insertRomaji(char ch2, char ch1, char ch0, int aPosition) {
      if (aPosition == -1) {
         aPosition = super._caretPosition;
      }

      aPosition *= 3;
      if (aPosition >= 0 && aPosition <= this._romaji.length()) {
         this._romaji.insert(aPosition, ch0);
         this._romaji.insert(aPosition, ch1);
         this._romaji.insert(aPosition, ch2);
      }
   }

   public boolean deleteRomajis(boolean isForward, int aPosition, int aCount) {
      if (aPosition == -1) {
         aPosition = super._caretPosition;
      }

      aPosition *= 3;
      aCount *= 3;
      int len = this._romaji.length();
      if (len == 0 || aPosition < 0 || aPosition > len || aCount < 0) {
         return false;
      }

      if (!isForward) {
         int start = aPosition - aCount;
         if (start < 0) {
            start = 0;
         }

         this._romaji.delete(start, aPosition);
         return true;
      } else {
         this._romaji.delete(aPosition, aPosition + aCount);
         return true;
      }
   }

   @Override
   public boolean deleteChar(boolean isForward, int aPosition) {
      boolean resultRomaji = this.deleteRomajis(isForward, aPosition, 1);
      boolean resultOriginal = super.deleteChar(isForward, aPosition);
      return resultRomaji && resultOriginal;
   }

   @Override
   public boolean deleteChars(boolean isForward, int aPosition, int aCount) {
      boolean resultRomaji = this.deleteRomajis(isForward, aPosition, aCount);
      boolean resultOriginal = super.deleteChars(isForward, aPosition, aCount);
      return resultRomaji && resultOriginal;
   }

   @Override
   public void setOriginal(String aValue) {
      this._romaji.setLength(aValue.length() * 3);
      super.setOriginal(aValue);
   }

   public void insertChar(char aChar, int aPosition, char ch2, char ch1, char ch0) {
      this.insertRomaji(ch2, ch1, ch0, aPosition);
      super.insertChar(aChar, aPosition);
   }

   public void insertChar(char aChar, int aPosition, char[] romaji) {
      this.insertRomajis(romaji, aPosition);
      super.insertChar(aChar, aPosition);
   }

   public void insertChars(char[] aChars, int aPosition, char[] romaji) {
      this.insertRomajis(romaji, aPosition);
      super.insertChars(aChars, aPosition);
   }

   public void setCharAt(int aPosition, char hira, char ch2, char ch1, char ch0) {
      StringBuffer original = this.getOriginal();
      if (aPosition == -1) {
         aPosition = super._caretPosition;
      }

      if (aPosition >= 0 && aPosition < original.length()) {
         original.setCharAt(aPosition, hira);
         aPosition *= 3;
         if (aPosition >= 0 && aPosition <= this._romaji.length() - 3) {
            this._romaji.setCharAt(aPosition++, ch2);
            this._romaji.setCharAt(aPosition++, ch1);
            this._romaji.setCharAt(aPosition, ch0);
         }
      }
   }

   public boolean insertSegmentOfThisVariantsToAnotherVariants(int thisOffset, int length, JapaneseVariants vars, int varOffset) {
      StringBuffer thisBuffer = this.getOriginal();
      int len = thisBuffer.length();
      if (length >= 0 && thisOffset >= 0 && thisOffset < len) {
         int end = thisOffset + length;
         if (end > len) {
            end = len;
         }

         StringBuffer varsBuffer = vars.getOriginal();
         if (varOffset >= 0 && varOffset <= varsBuffer.length()) {
            int index = varOffset;

            for (int i = thisOffset; i < end; i++) {
               varsBuffer.insert(index++, thisBuffer.charAt(i));
            }

            thisOffset *= 3;
            length *= 3;
            varOffset *= 3;
            len = this._romaji.length();
            if (thisOffset >= 0 && thisOffset < len) {
               end = thisOffset + length;
               if (end > len) {
                  end = len;
               }

               varsBuffer = vars._romaji;
               if (varOffset >= 0 && varOffset <= varsBuffer.length()) {
                  for (int var15 = thisOffset; var15 < end; var15++) {
                     varsBuffer.insert(varOffset++, this._romaji.charAt(var15));
                  }

                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void arrayResize(Object array, int size) {
      if (size < 4096) {
         Array.resize(array, size);
      } else {
         int currentSize = 0;
         if (!(array instanceof byte[])) {
            currentSize = ((char[])array).length;
         } else {
            currentSize = ((byte[])array).length;
         }

         Array.extend(array, size - currentSize);
      }
   }

   public void copyVariants(char[] words, byte[] lengths, int count, int defaultLength) {
      this.copyVariants(words, lengths, count, defaultLength, true);
   }

   public void copyVariants(char[] words, byte[] lengths, int count, int defaultLength, boolean toUpdate) {
      char[] currentWords = super._variants._words;
      byte[] currentLengths = super._variants._lengths;
      int totalLength = Arrays.sum(lengths, 0, count, false);
      if (totalLength > currentWords.length) {
         this.arrayResize(currentWords, totalLength);
      }

      if (totalLength < defaultLength && currentWords.length > defaultLength) {
         this.arrayResize(currentWords, defaultLength);
      }

      if (count > currentLengths.length) {
         this.arrayResize(currentLengths, count);
      }

      if (count < currentLengths.length && currentLengths.length > defaultLength) {
         this.arrayResize(currentLengths, defaultLength);
      }

      System.arraycopy(words, 0, currentWords, 0, totalLength);
      System.arraycopy(lengths, 0, currentLengths, 0, count);
      super._variants._count = count;
      super._variants._totalLength = totalLength;
      super._variants.resetIteration();
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void copyVariant(char[] words, int offset, int length, int defaultLength) {
      this.copyVariant(words, offset, length, defaultLength, true);
   }

   public void copyVariant(char[] words, int offset, int length, int defaultLength, boolean toUpdate) {
      char[] currentWords = super._variants._words;
      byte[] currentLengths = super._variants._lengths;
      if (length > currentWords.length) {
         this.arrayResize(currentWords, length);
      }

      if (length < defaultLength && currentWords.length > defaultLength) {
         this.arrayResize(currentWords, defaultLength);
      }

      if (currentLengths.length < 1 || currentLengths.length > defaultLength) {
         this.arrayResize(currentLengths, defaultLength);
      }

      System.arraycopy(words, offset, currentWords, 0, length);
      currentLengths[0] = (byte)length;
      super._variants._count = 1;
      super._variants._totalLength = length;
      super._variants.resetIteration();
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void appendWord(StringBuffer word, int offset, int length) {
      this.appendWord(word, offset, length, true);
   }

   public void appendWord(StringBuffer word, int offset, int length, boolean toUpdate) {
      char[] currentWords = super._variants._words;
      byte[] currentLengths = super._variants._lengths;
      int count = super._variants._count;
      int currentLength = Arrays.sum(currentLengths, 0, count, false);
      int newLength = currentLength + length;
      if (newLength > currentWords.length) {
         this.arrayResize(currentWords, newLength);
      }

      if (count + 1 > currentLengths.length) {
         this.arrayResize(currentLengths, count + 1);
      }

      word.getChars(offset, offset + length, currentWords, currentLength);
      currentLengths[count] = (byte)length;
      super._variants._count = count + 1;
      super._variants._totalLength = newLength;
      super._variants.resetIteration();
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void appendChars(char[] chars, int amount) {
      this.appendChars(chars, amount, true);
   }

   public void appendChars(char[] chars, int amount, boolean toUpdate) {
      char[] currentWords = super._variants._words;
      byte[] currentLengths = super._variants._lengths;
      int count = super._variants._count;
      int currentLength = Arrays.sum(currentLengths, 0, count, false);
      int newLength = currentLength + amount;
      if (newLength > currentWords.length) {
         this.arrayResize(currentWords, newLength);
      }

      super._variants._totalLength = newLength;
      newLength = count + amount;
      if (newLength > currentLengths.length) {
         this.arrayResize(currentLengths, newLength);
      }

      super._variants._count = newLength;

      for (int i = 0; i < amount; i++) {
         currentWords[currentLength + i] = chars[i];
         currentLengths[count + i] = 1;
      }

      super._variants.resetIteration();
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void fixQuestionMark() {
      StringBuffer original = this.getOriginal();
      if (original.length() != 0) {
         char ch = original.charAt(0);
         if (super._variants._lengths[0] == 1 && super._variants._words[0] == '?') {
            super._variants._words[0] = ch;
         }
      }
   }
}
