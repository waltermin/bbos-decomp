package net.rim.tid.util;

import net.rim.device.api.util.Arrays;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.itie.ISecureInputMethodBuffer;
import net.rim.vm.Array;

public class SLTextDataContainer implements ISecureInputMethodBuffer {
   protected int _index;
   private int _wordPtr;
   public char[] _words;
   public byte[] _lengths;
   public int _count;
   public int _totalLength;
   private static final int INC = 10;

   public boolean addVariant(StringBuffer aVariant) {
      sanityCheck(this);
      int length = aVariant.length();
      this.insureAddCapacity(length, 1);
      aVariant.getChars(0, length, this._words, this._totalLength);
      this._lengths[this._count++] = (byte)length;
      this._totalLength += length;
      return true;
   }

   public boolean addVariant(SLCurrentVariant aVariant) {
      sanityCheck(this);
      this.insureAddCapacity(aVariant._length, 1);
      System.arraycopy(aVariant._variants, aVariant._offset, this._words, this._totalLength, aVariant._length);
      this._lengths[this._count++] = (byte)aVariant._length;
      this._totalLength = this._totalLength + aVariant._length;
      return true;
   }

   public void addVariants(SLTextDataContainer aText) {
      sanityCheck(this);
      sanityCheck(aText);
      this.insureAddCapacity(aText._totalLength, aText._count);
      System.arraycopy(aText._words, 0, this._words, this._totalLength, aText._totalLength);
      System.arraycopy(aText._lengths, 0, this._lengths, this._count, aText._count);
      this._count = this._count + aText._count;
      this._totalLength = this._totalLength + aText._totalLength;
   }

   public boolean deleteVariantAt(int index) {
      sanityCheck(this);
      if (index < 0 || index >= this._count) {
         return false;
      }

      if (this._count == 0) {
         return true;
      }

      int wordPtr = Arrays.sum(this._lengths, 0, index, false);
      int length = this._lengths[index];
      if (index < this._count - 1) {
         System.arraycopy(this._words, wordPtr + length, this._words, wordPtr, this._totalLength - wordPtr - length);
         System.arraycopy(this._lengths, index + 1, this._lengths, index, 1);
      }

      this._count--;
      this._totalLength -= length;
      return true;
   }

   public boolean next(SLCurrentVariant result) {
      if (!this.get(result)) {
         return false;
      }

      this.next();
      return true;
   }

   public boolean next(StringBuffer result) {
      if (!this.get(result)) {
         return false;
      }

      this.next();
      return true;
   }

   public boolean get(SLCurrentVariant result) {
      if (this._index >= this._count) {
         return false;
      }

      result.set(this._words, this._wordPtr, this._lengths[this._index]);
      return true;
   }

   public boolean get(StringBuffer result) {
      if (this._index >= this._count) {
         return false;
      }

      result.setLength(0);
      result.append(this._words, this._wordPtr, this._lengths[this._index]);
      return true;
   }

   public void addCurrentTo(StringBuffer toAdd) {
      toAdd.append(this._words, this._wordPtr, this._lengths[this._index]);
   }

   public boolean hasMoreElements() {
      return this._index < this._count;
   }

   public boolean seek(int index) {
      if (index >= this._count) {
         return false;
      }

      if (index < this._index) {
         this._wordPtr = 0;
         this._index = 0;
      }

      for (int i = this._index; i < index; i++) {
         this.next();
      }

      return true;
   }

   public void next() {
      this._wordPtr = this._wordPtr + this._lengths[this._index];
      this._index++;
   }

   public void prev() {
      this._index--;
      this._wordPtr = this._wordPtr - this._lengths[this._index];
   }

   public void resetIteration() {
      this._index = 0;
      this._wordPtr = 0;
   }

   public void init(char[] words, byte[] length, int count) {
      this._words = words;
      this._lengths = length;
      this._count = count;
      this._totalLength = 0;
      if (length != null) {
         this._totalLength = Arrays.sum(length, 0, count, false);
      }

      this.resetIteration();
   }

   public final void toLowerCase() {
      int ptr = 0;
      int wordIdx = 0;

      for (int offInWord = 0; wordIdx < this._count && ptr < this._words.length; offInWord++) {
         this._words[ptr] = Utils.toLowerCase(this._words[ptr]);
         if (offInWord == this._lengths[wordIdx] - 1) {
            offInWord = -1;
            wordIdx++;
         }

         ptr++;
      }
   }

   public void permute(int[] index, SLTextDataContainer result) {
      int total = Math.min(this._count, index.length);
      if (result._words.length < this._totalLength) {
         Array.resize(result._words, this._totalLength + 10);
      }

      if (result._lengths.length < this._count) {
         Array.resize(result._lengths, this._count);
      }

      result._count = 0;
      result._totalLength = 0;
      int resultPtr = 0;

      for (int i = 0; i < total; i++) {
         int currentVariantIndex = index[i];
         if (currentVariantIndex >= 0 && currentVariantIndex < this._count) {
            int len = this._lengths[currentVariantIndex];
            System.arraycopy(this._words, Arrays.sum(this._lengths, 0, currentVariantIndex, false), result._words, resultPtr, len);
            resultPtr += len;
            result._lengths[i] = (byte)len;
            result._count++;
         }
      }

      result._totalLength = resultPtr;
   }

   @Override
   public boolean runSecureClean() {
      this.resetIteration();
      if (this._words != null) {
         Arrays.fill(this._words, '\u0000');
      }

      if (this._lengths != null) {
         Arrays.fill(this._lengths, (byte)0);
      }

      this._count = 0;
      this._totalLength = 0;
      return false;
   }

   private void insureAddCapacity(int aLength, int aCount) {
      if (this._totalLength + aLength >= this._words.length) {
         Array.resize(this._words, this._totalLength + aLength + 10);
      }

      if (this._count + aCount >= this._lengths.length) {
         Array.resize(this._lengths, this._count + aCount);
      }
   }

   private static void sanityCheck(SLTextDataContainer toCheck) {
      if (toCheck._count != 0 && toCheck._totalLength == 0) {
         throw new Object();
      }

      if (Arrays.sum(toCheck._lengths, 0, toCheck._count, false) != toCheck._totalLength) {
         throw new Object();
      }
   }

   public SLTextDataContainer() {
      this(false);
   }

   public SLTextDataContainer(boolean allocateDataBuffers) {
      InputContext.getInstance(false).addSecureBuffer(this);
      if (allocateDataBuffers) {
         this._words = new char[0];
         this._lengths = new byte[0];
      }
   }
}
