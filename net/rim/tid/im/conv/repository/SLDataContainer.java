package net.rim.tid.im.conv.repository;

import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.util.SLTextDataContainer;
import net.rim.vm.Array;

public class SLDataContainer {
   char[] _frequencyInf;
   boolean _freqAlreadyRequested = true;
   StringBuffer[] _buffer;
   boolean _strAlreadyRequested;
   int[] _posInf;
   boolean _posAlreadyRequested;
   boolean[] _modifiedFrequency;
   boolean[] _validWords;
   public int[] _param1;
   public int[] _param2;
   private int _variantsCount;
   private char[] _words;
   private byte[] _lengths;
   private byte[] _order;
   private short[] _offsets;
   private int[] _hashCodes;
   private int _dataLength;
   private char[] _prefixes;
   private int _prefixNo;
   private SLCurrentVariant _suffix;
   private SLTextDataContainer _dataContainer = (SLTextDataContainer)(new Object());

   public SLDataContainer(int aSize) {
      if (aSize > 0) {
         this._buffer = new Object[aSize];

         for (int i = 0; i < aSize; i++) {
            this._buffer[i] = (StringBuffer)(new Object());
         }
      }
   }

   public int[] getPOS() {
      if (this._posAlreadyRequested) {
         System.err.println("Warning: requesting pos from data container more then 1 time");
      } else {
         this._posAlreadyRequested = true;
      }

      return this._posInf;
   }

   public void setPOS(int[] pos) {
      if (this._posInf != null && !this._posAlreadyRequested) {
         System.err.println("Warning: pos was not requested ones");
      } else {
         this._posAlreadyRequested = false;
      }

      this._posInf = pos;
   }

   public char[] getFrequency() {
      return this._frequencyInf;
   }

   public void setFrequency(char[] frq) {
      this._frequencyInf = frq;
   }

   public boolean[] getModificationMatrix() {
      return this._modifiedFrequency;
   }

   public void setModifiationMatrix(boolean[] matrix) {
      this._modifiedFrequency = matrix;
   }

   public void setValidWordsMatrix(boolean[] matrix) {
      this._validWords = matrix;
   }

   public boolean[] getValidWordsMatrix() {
      return this._validWords;
   }

   public StringBuffer[] getBuffer() {
      return this._buffer;
   }

   public void resetAll() {
      this._frequencyInf = null;
      this._posInf = null;
      this._modifiedFrequency = null;
      this._validWords = null;
      this._words = null;
      this._lengths = null;
      this._variantsCount = 0;
      this.resetBuffer();
   }

   public void resetBuffer() {
      if (this._buffer != null) {
         for (int i = 0; i < this._buffer.length; i++) {
            this._buffer[i].setLength(0);
         }
      }
   }

   public char[] getWords() {
      return this._words;
   }

   public byte[] getLength() {
      return this._lengths;
   }

   public void setWords(char[] aWords, byte[] aLengths) {
      this._words = aWords;
      this._lengths = aLengths;
      if (aLengths != null) {
         this._variantsCount = aLengths.length;
      }
   }

   public byte[] getLengths() {
      return this._lengths;
   }

   public void setVariantsCount(int aCount) {
      this._variantsCount = aCount;
   }

   public int getVariantsCount() {
      return this._variantsCount;
   }

   public int getResults(char[] aWords, byte[] aLengths, int wordsOffset, int lengthsOffset) {
      for (int i = 0; i < this._variantsCount; i++) {
         int length = this._buffer[i].length();
         if (wordsOffset + length > aWords.length) {
            this._variantsCount = i;
            break;
         }

         this._buffer[i].getChars(0, length, aWords, wordsOffset);
         wordsOffset += length;
         aLengths[i + lengthsOffset] = (byte)length;
      }

      return this._variantsCount;
   }

   public byte[] getOrder() {
      return this._order;
   }

   public void setOrder(byte[] aOrder) {
      this._order = aOrder;
   }

   public short[] getOffsets() {
      return this._offsets;
   }

   public void setOffsets(short[] aOffsets) {
      this._offsets = aOffsets;
   }

   public int[] getHashCodes() {
      return this._hashCodes;
   }

   public void setHashCodes(int[] aHashCodes) {
      this._hashCodes = aHashCodes;
   }

   public int getDataLength() {
      return this._dataLength;
   }

   public void setDataLength(int aDataLength) {
      this._dataLength = aDataLength;
   }

   public void setPrefixes(char[] aPrefixes, int aPrefixLen, int aPrefixNo) {
      this._prefixes = aPrefixes;
      this._prefixNo = aPrefixNo;
   }

   public char[] getPrefixes() {
      return this._prefixes;
   }

   public int getPrefixNo() {
      return this._prefixNo;
   }

   public void setResults(SLVariants aVariant) {
      int suffixLen = this._suffix == null ? 0 : this._suffix._length;
      this._dataContainer.init(null, null, 0);
      aVariant.setAdditionalArrays(this._dataContainer);
      aVariant.getVariants(this._dataContainer);
      this._dataContainer._count = this._variantsCount;
      if (this._variantsCount == 0) {
         aVariant.setVariants(this._dataContainer);
      } else {
         if (this._variantsCount > this._dataContainer._lengths.length) {
            Array.resize(this._dataContainer._lengths, this._variantsCount);
         }

         if (this._dataLength + this._variantsCount * suffixLen > this._dataContainer._words.length) {
            Array.resize(this._dataContainer._words, this._dataLength + this._variantsCount * suffixLen);
         }

         if (this._order != null) {
            int index = 0;
            int start = 0;
            int newStart = 0;

            for (int i = 0; i < this._variantsCount; i++) {
               start = this.getStartFor(this._order[i], this._lengths, index, start);
               byte length = this._lengths[this._order[i]];
               this._dataContainer._lengths[i] = (byte)(length + suffixLen);
               System.arraycopy(this._words, start, this._dataContainer._words, newStart, length);
               newStart += length;
               if (suffixLen != 0) {
                  this._suffix.copyInto(this._dataContainer._words, newStart);
                  newStart += this._suffix._length;
               }

               index = this._order[i];
            }
         } else {
            System.arraycopy(this._words, 0, this._dataContainer._words, 0, this._dataLength);
            if (this._lengths == null) {
               for (int i = 0; i < this._variantsCount; i++) {
                  this._dataContainer._lengths[i] = 1;
               }
            } else {
               System.arraycopy(this._lengths, 0, this._dataContainer._lengths, 0, this._variantsCount);
            }
         }

         aVariant.setVariants(this._dataContainer);
         aVariant.setVariantIndex(-1);
      }
   }

   private int getStartFor(int index, byte[] length, int cachedIndex, int cachedLen) {
      if (cachedIndex > index) {
         cachedIndex = 0;
         cachedLen = 0;
      }

      while (cachedIndex < index) {
         cachedLen += length[cachedIndex];
         cachedIndex++;
      }

      return cachedLen;
   }

   public void getVariant(int aIndex, SLCurrentVariant aVariant) {
      this.getVariant(aIndex, aVariant, false);
   }

   public void getVariant(int aIndex, SLCurrentVariant aVariant, boolean ignoreOrder) {
      aVariant._variants = this._words;
      int id = this._order != null && !ignoreOrder ? this._order[aIndex] : aIndex;
      int offset = 0;
      if (this._offsets == null) {
         for (int i = 0; i < aIndex; i++) {
            offset += this._lengths[i];
         }
      } else {
         offset = this._offsets[id];
      }

      aVariant._offset = offset;
      aVariant._length = this._lengths == null ? 1 : this._lengths[id];
   }

   public void getPrefixAt(int aIndex, SLCurrentVariant result) {
      result._variants = this._prefixes;
      int len = this._prefixes.length / this._prefixNo;
      result._offset = aIndex * len;
      result._length = len;
   }

   public void setSuffix(SLCurrentVariant aSuffix) {
      this._suffix = aSuffix;
   }

   public SLCurrentVariant getSuffix() {
      return this._suffix;
   }
}
