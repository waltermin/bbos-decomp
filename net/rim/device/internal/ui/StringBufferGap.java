package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.vm.Array;

public final class StringBufferGap implements AbstractString {
   private Object _text;
   private int _gapStart;
   private int _gapLength;
   private int _size;
   private StringBufferGap$SBGAbstractString _abstractString = new StringBufferGap$SBGAbstractString(this);
   private static final int GAPSIZE = 100;

   public final void set(StringBuffer s) {
      int startSize = 10;
      if (this._text == null) {
         this._text = new byte[startSize];
      } else if (!(this._text instanceof char[])) {
         startSize = ((byte[])this._text).length;
      } else {
         startSize = ((char[])this._text).length;
      }

      this._gapStart = 0;
      this._gapLength = startSize;
      this._size = 0;
      this.insert(s);
   }

   public final void setLabelLength(int length) {
      this._abstractString.setLabelLength(length);
   }

   public final void clear() {
      int startSize = 10;
      this._text = new byte[startSize];
      this._gapStart = 0;
      this._gapLength = startSize;
      this._size = 0;
   }

   public final void wipe() {
      if (!(this._text instanceof char[])) {
         Arrays.fill((byte[])this._text, (byte)0, this._gapStart, this._gapLength);
      } else {
         Arrays.fill((char[])this._text, '\u0000', this._gapStart, this._gapLength);
      }
   }

   public final void delete(int count) {
      if (count < 0) {
         throw new IndexOutOfBoundsException();
      }

      count = Math.min(this._gapStart, count);
      this._gapStart -= count;
      this._gapLength += count;
      this._size -= count;
   }

   public final int draw(Graphics graphics, int x, int y, int offset, int length) {
      return graphics.drawText(this, offset, length, x, y, 0, -1);
   }

   public final AbstractString getAbstractString() {
      return this._abstractString;
   }

   public final void seek(int index) {
      if (index < this._gapStart) {
         System.arraycopy(this._text, index, this._text, this._gapLength + index, this._gapStart - index);
      } else {
         System.arraycopy(this._text, this._gapStart + this._gapLength, this._text, this._gapStart, index - this._gapStart);
      }

      this._gapStart = index;
   }

   public final StringBuffer getPrevWord() {
      int index = this.previousIndexOf(" \n") + 1;
      String returnString;
      if (!(this._text instanceof char[])) {
         returnString = new String((byte[])this._text, index, this._gapStart - index);
      } else {
         char[] text = (char[])this._text;
         returnString = new String(text, index, this._gapStart - index);
      }

      return new StringBuffer(returnString);
   }

   public final StringBuffer getPrevChars(int len) {
      StringBuffer buffer = new StringBuffer();
      if (this._text instanceof char[]) {
         char[] text = (char[])this._text;
         buffer.append(text, 0, Math.min(len, this._gapStart));
         len = Math.max(0, len - this._gapStart);
         buffer.append(text, this._gapStart + this._gapLength, len);
         return buffer;
      }

      int i = this._gapStart - 1;

      for (int count = 0; count < len; count++) {
         buffer.append(this.charAt(i));
         i--;
      }

      buffer.reverse();
      return buffer;
   }

   public final int getPrevNonSpaceCharOffset() {
      return this.previousIndexOf((char)32, false);
   }

   public final String getText(int offset, int length) {
      if (offset >= 0 && offset + length <= this._size && length >= 0) {
         int before = MathUtilities.clamp(0, length + offset, this._gapStart) - offset;
         int after = length - before;
         if (after != 0) {
            StringBuffer buffer = new StringBuffer(length);
            if (!(this._text instanceof char[])) {
               byte[] text = (byte[])this._text;
               if (before > 0) {
                  buffer.append(new String(text, offset, before));
               }

               if (after > 0) {
                  int startAfter = this._gapStart + this._gapLength;
                  if (before < 0) {
                     startAfter -= before;
                     after += before;
                  }

                  buffer.append(new String(text, startAfter, after));
               }
            } else {
               char[] text = (char[])this._text;
               if (before > 0) {
                  buffer.append(text, offset, before);
               }

               if (after > 0) {
                  int startAfter = this._gapStart + this._gapLength;
                  if (before < 0) {
                     startAfter -= before;
                     after += before;
                  }

                  buffer.append(text, startAfter, after);
               }
            }

            return buffer.toString();
         } else if (!(this._text instanceof char[])) {
            byte[] var8 = (byte[])this._text;
            return new String(var8, offset, before);
         } else {
            char[] text = (char[])this._text;
            return new String(text, offset, before);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final void getText(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      int length = srcEnd - srcBegin;
      if (srcBegin >= 0 && srcBegin + length <= this._size && length >= 0 && dstBegin >= 0 && dstBegin + length <= dst.length) {
         int before = MathUtilities.clamp(0, length + srcBegin, this._gapStart) - srcBegin;
         int after = length - before;
         if (!(this._text instanceof char[])) {
            byte[] text = (byte[])this._text;
            if (before > 0) {
               for (int lv = 0; lv < before; lv++) {
                  dst[dstBegin + lv] = (char)(text[srcBegin + lv] & 0xFF);
               }
            }

            if (after > 0) {
               int startAfter = this._gapStart + this._gapLength + srcBegin;
               if (before < 0) {
                  startAfter -= before;
                  after += before;
               }

               for (int lv = 0; lv < after; lv++) {
                  dst[dstBegin + before + lv] = (char)(text[startAfter + lv] & 0xFF);
               }
            }
         } else {
            char[] text = (char[])this._text;
            if (before > 0) {
               System.arraycopy(text, srcBegin, dst, dstBegin, before);
            }

            if (after > 0) {
               int startAfter = this._gapStart + this._gapLength;
               if (before < 0) {
                  startAfter -= before;
                  after += before;
               }

               System.arraycopy(text, startAfter, dst, dstBegin + before, after);
               return;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final void insert(char c) {
      if (this._gapLength <= 0) {
         this.grow(100);
      }

      if (!(this._text instanceof char[])) {
         if ((c & '\uff00') != 0) {
            this.promote();
            ((char[])this._text)[this._gapStart] = c;
         } else {
            ((byte[])this._text)[this._gapStart] = (byte)c;
         }
      } else {
         ((char[])this._text)[this._gapStart] = c;
      }

      this._gapLength--;
      this._gapStart++;
      this._size++;
   }

   public final void insert(String s) {
      if (s != null && s.length() != 0) {
         int slen = s.length();
         if (slen > this._gapLength) {
            this.grow(slen);
         }

         if (!(this._text instanceof char[])) {
            int len = slen;

            while (--len >= 0) {
               if ((s.charAt(len) & '\uff00') != 0) {
                  this.promote();
                  char[] text = (char[])this._text;
                  s.getChars(0, slen, text, this._gapStart);
                  int lenx = s.length();
                  this._gapStart += lenx;
                  this._size += lenx;
                  this._gapLength -= lenx;
                  return;
               }
            }

            byte[] chars = s.getBytes();
            System.arraycopy(chars, 0, this._text, this._gapStart, chars.length);
         } else {
            char[] text = (char[])this._text;
            s.getChars(0, slen, text, this._gapStart);
         }

         int len = s.length();
         this._gapStart += len;
         this._size += len;
         this._gapLength -= len;
      }
   }

   public final void insert(StringBuffer s) {
      if (s != null && s.length() != 0) {
         int slen = s.length();
         if (slen > this._gapLength) {
            this.grow(slen);
         }

         if (!(this._text instanceof char[])) {
            int len = slen;

            while (--len >= 0) {
               if ((s.charAt(len) & '\uff00') != 0) {
                  this.promote();
                  char[] text = (char[])this._text;
                  s.getChars(0, slen, text, this._gapStart);
                  int lenx = s.length();
                  this._gapStart += lenx;
                  this._size += lenx;
                  this._gapLength -= lenx;
                  return;
               }
            }

            byte[] text = (byte[])this._text;

            for (int lv = 0; lv < slen; lv++) {
               text[this._gapStart + lv] = (byte)s.charAt(lv);
            }
         } else {
            char[] text = (char[])this._text;
            s.getChars(0, slen, text, this._gapStart);
         }

         int len = s.length();
         this._gapStart += len;
         this._size += len;
         this._gapLength -= len;
      }
   }

   public final void insert(StringBufferGap s) {
      if (s != null && s.length() != 0) {
         int slen = s.length();
         if (slen > this._gapLength) {
            this.grow(slen);
         }

         if (!(this._text instanceof char[])) {
            int len = slen;

            while (--len >= 0) {
               if ((s.charAt(len) & '\uff00') != 0) {
                  this.promote();
                  char[] text = (char[])this._text;
                  s.getChars(0, slen, text, this._gapStart);
                  int lenx = s.length();
                  this._gapStart += lenx;
                  this._size += lenx;
                  this._gapLength -= lenx;
                  return;
               }
            }

            byte[] text = (byte[])this._text;

            for (int lv = 0; lv < slen; lv++) {
               text[this._gapStart + lv] = (byte)s.charAt(lv);
            }
         } else {
            char[] text = (char[])this._text;
            s.getChars(0, slen, text, this._gapStart);
         }

         int len = s.length();
         this._gapStart += len;
         this._size += len;
         this._gapLength -= len;
      }
   }

   public final int indexOf(char c, boolean match) {
      if (!(this._text instanceof char[])) {
         if ((c & '\uff00') != 0) {
            return -1;
         }

         byte[] text = (byte[])this._text;
         byte bc = (byte)c;

         for (int i = 0; i < this._size; i++) {
            int index = i;
            if (index >= this._gapStart) {
               index += this._gapLength;
            }

            if (match && text[index] == bc || !match && text[index] != bc) {
               return i;
            }
         }
      } else {
         char[] text = (char[])this._text;

         for (int i = 0; i < this._size; i++) {
            int index = i;
            if (index >= this._gapStart) {
               index += this._gapLength;
            }

            if (match && text[index] == c || !match && text[index] != c) {
               return i;
            }
         }
      }

      return -1;
   }

   public final int previousIndexOf(char c) {
      return this.previousIndexOf(c, true);
   }

   public final int previousIndexOf(String s) {
      return this.previousIndexOf(s, true);
   }

   public final int previousIndexOf(char c, boolean match) {
      if (!(this._text instanceof char[])) {
         if ((c & '\uff00') != 0) {
            return -1;
         }

         byte[] text = (byte[])this._text;
         byte bc = (byte)c;

         for (int i = this._gapStart - 1; i >= 0; i--) {
            if (match && text[i] == bc || !match && text[i] != bc) {
               return i;
            }
         }
      } else {
         char[] text = (char[])this._text;

         for (int i = this._gapStart - 1; i >= 0; i--) {
            if (match && text[i] == c || !match && text[i] != c) {
               return i;
            }
         }
      }

      return -1;
   }

   public final int previousIndexOf(String s, boolean match) {
      if (!(this._text instanceof char[])) {
         byte[] text = (byte[])this._text;

         for (int i = this._gapStart - 1; i >= 0; i--) {
            char c = (char)(text[i] & 0xFF);
            if (match && s.indexOf(c) != -1 || !match && s.indexOf(c) == -1) {
               return i;
            }
         }
      } else {
         char[] text = (char[])this._text;

         for (int i = this._gapStart - 1; i >= 0; i--) {
            if (match && s.indexOf(text[i]) != -1 || !match && s.indexOf(text[i]) == -1) {
               return i;
            }
         }
      }

      return -1;
   }

   public final int indexOf(char c) {
      return this.indexOf(c, true);
   }

   @Override
   public final int length() {
      return this._size;
   }

   @Override
   public final int indexOf(char ch, int startIndex, int endIndex) {
      int idxStartA;
      int idxStartB;
      if (startIndex < this._gapStart) {
         idxStartA = startIndex;
         idxStartB = this._gapStart + this._gapLength;
      } else {
         idxStartA = this._gapStart;
         idxStartB = startIndex + this._gapLength;
      }

      int idxEndA;
      int idxEndB;
      if (endIndex < this._gapStart) {
         idxEndA = endIndex;
         idxEndB = this._gapStart + this._gapLength;
      } else {
         idxEndA = this._gapStart;
         idxEndB = endIndex + this._gapLength;
      }

      if (!(this._text instanceof char[])) {
         byte[] text = (byte[])this._text;
         byte cbyte = (byte)ch;

         for (int idx = idxStartA; idx < idxEndA; idx++) {
            if (text[idx] == cbyte) {
               return idx;
            }
         }

         for (int idx = idxStartB; idx < idxEndB; idx++) {
            if (text[idx] == cbyte) {
               return idx - this._gapLength;
            }
         }
      } else {
         char[] text = (char[])this._text;

         for (int idx = idxStartA; idx < idxEndA; idx++) {
            if (text[idx] == ch) {
               return idx;
            }
         }

         for (int idx = idxStartB; idx < idxEndB; idx++) {
            if (text[idx] == ch) {
               return idx - this._gapLength;
            }
         }
      }

      return -1;
   }

   @Override
   public final void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      int countBefore = Math.max(0, Math.min(srcEnd - srcBegin, this._gapStart - srcBegin));
      int countAfter = Math.max(0, Math.min(srcEnd - srcBegin, srcEnd - this._gapStart));
      if (!(this._text instanceof char[])) {
         byte[] text = (byte[])this._text;
         int srcIndex = srcBegin;
         int dstIndex = dstBegin;

         while (countBefore-- > 0) {
            dst[dstIndex++] = (char)(text[srcIndex++] & 0xFF);
         }

         srcIndex += this._gapLength;

         while (countAfter-- > 0) {
            dst[dstIndex++] = (char)(text[srcIndex++] & 0xFF);
         }
      } else {
         char[] text = (char[])this._text;
         if (countBefore > 0) {
            System.arraycopy(text, srcBegin, dst, dstBegin, countBefore);
         }

         if (countAfter > 0) {
            System.arraycopy(text, srcBegin + countBefore + this._gapLength, dst, dstBegin + countBefore, countAfter);
            return;
         }
      }
   }

   @Override
   public final char charAt(int index) {
      if (index >= this._gapStart) {
         index += this._gapLength;
      }

      return !(this._text instanceof char[]) ? (char)(((byte[])this._text)[index] & 0xFF) : ((char[])this._text)[index];
   }

   public StringBufferGap() {
      this.clear();
   }

   @Override
   public final int hashCode() {
      int hash = 0;
      if (!(this._text instanceof char[])) {
         byte[] text = (byte[])this._text;

         for (int arrayLength = 0; arrayLength < this._gapStart; arrayLength++) {
            hash = hash * 31 + text[arrayLength];
         }

         int arrayLength = text.length;

         for (int idx = this._gapStart + this._gapLength; idx < arrayLength; idx++) {
            hash = hash * 31 + text[idx];
         }
      } else {
         char[] text = (char[])this._text;

         for (int idx = 0; idx < this._gapStart; idx++) {
            hash = hash * 31 + text[idx];
         }

         int arrayLength = text.length;

         for (int idx = this._gapStart + this._gapLength; idx < arrayLength; idx++) {
            hash = hash * 31 + text[idx];
         }
      }

      return hash;
   }

   public StringBufferGap(StringBuffer s) {
      this.clear();
      this.insert(s);
   }

   private final void promote() {
      if (this._text instanceof char[]) {
         throw new IllegalStateException();
      }

      byte[] oldText = (byte[])this._text;
      char[] newText = new char[oldText.length];
      int lv = oldText.length;

      while (--lv >= 0) {
         newText[lv] = (char)(oldText[lv] & 0xFF);
      }

      this._text = newText;
   }

   private final void grow(int count) {
      int length = !(this._text instanceof char[]) ? ((byte[])this._text).length : ((char[])this._text).length;
      Array.resize(this._text, length + count);
      int start = this._gapStart + this._gapLength;
      System.arraycopy(this._text, start, this._text, start + count, length - start);
      this._gapLength += count;
   }

   public StringBufferGap(String s) {
      this.clear();
      this.insert(s);
   }

   @Override
   public final String toString() {
      return this.getText(0, this._size);
   }

   private static final native int stringToWords0(StringBufferGap var0, int var1, int var2, int[] var3, int[] var4);

   public static final int stringToWords(StringBufferGap SBG, int startIndex, int length, int[] offsets, int[] lengths) {
      int wordCount = 0;
      Array.resize(offsets, (length >> 1) + 1);
      Array.resize(lengths, (length >> 1) + 1);

      try {
         wordCount = stringToWords0(SBG, startIndex, length, offsets, lengths);
      } catch (ArrayIndexOutOfBoundsException exc) {
         System.err.println("Internal error!");
      }

      return wordCount;
   }
}
