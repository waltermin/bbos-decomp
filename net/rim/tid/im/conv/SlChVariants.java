package net.rim.tid.im.conv;

import net.rim.device.api.util.Arrays;
import net.rim.tid.util.SLTextDataContainer;

public class SlChVariants extends SLVariants {
   private int _cachedVariantIndex = -1;
   private int _savedVIndex = -1;
   private int _savedCIndex = -1;
   private byte _indexInSegment;
   private byte _segmentLength = 1;
   private byte _cachedIndexInSegment;
   private byte _cachedSegmentLength = 1;
   private boolean _isFixedVariantsSet;
   private boolean _wasOpened;
   private byte[] _variantsStartIndexes;
   private byte[] _wordsTones;
   private char[] _pins = new char[4];
   private int[] _pinsOffsets = new int[4];
   private char _originalCandidate = '\uffff';
   int _pinsCount;
   private int _removedIndex = -1;
   private char _removedVariant = '\uffff';
   public SlChVariants$BigramPair _wordlistPair = new SlChVariants$BigramPair();
   public SlChVariants$BigramPair _userPair = new SlChVariants$BigramPair();
   private boolean _isLocked;
   private StringBuffer _reading = new StringBuffer();
   private static StringBuffer _candBuffer = new StringBuffer();

   public SlChVariants(int aSize) {
      super(aSize);
   }

   @Override
   public boolean deleteChars(boolean isForward, int aPosition, int aCount) {
      int caretPosition = super._caretPosition;
      if (super.deleteChars(isForward, aPosition, aCount) && isForward && caretPosition > aPosition) {
         caretPosition -= aCount;
         super._caretPosition = caretPosition < 0 ? 0 : caretPosition;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setCaretPosition(int aPosition) {
      if (aPosition >= 0 && (aPosition <= this.currentVariantLength() || super._stickyOriginal && aPosition <= super._original.length())) {
         super._caretPosition = aPosition;
      }
   }

   public StringBuffer getReading() {
      return this._reading;
   }

   public void setReading(StringBuffer value) {
      this._reading.setLength(0);
      if (value != null) {
         this._reading.append(value);
      }
   }

   public void setOpenedFlag() {
      this._wasOpened = true;
   }

   public boolean getOpenedFlag() {
      return this._wasOpened;
   }

   public boolean isLocked() {
      return this._isLocked;
   }

   public void setLocked(boolean locked) {
      this._isLocked = locked;
   }

   public void saveIndexes() {
      this._savedCIndex = this._cachedVariantIndex;
      this._savedVIndex = super._currentVariantIndex;
      this._cachedIndexInSegment = this._indexInSegment;
      this._cachedSegmentLength = this._segmentLength;
   }

   public void restoreIndexes() {
      this._cachedVariantIndex = this._savedCIndex;
      this.setVariantIndex(this._savedVIndex);
      this._indexInSegment = this._cachedIndexInSegment;
      this._segmentLength = this._cachedSegmentLength;
   }

   @Override
   public void clear() {
      super.clear();
      this._cachedVariantIndex = this._removedIndex = -1;
      this._indexInSegment = this._cachedIndexInSegment = 0;
      this._segmentLength = this._cachedSegmentLength = 1;
      this._isFixedVariantsSet = this._wasOpened = this._isLocked = false;
      this._variantsStartIndexes = null;
      this._pinsCount = 0;
      _candBuffer.setLength(0);
      this._originalCandidate = this._removedVariant = '\uffff';
      this._wordlistPair.reset();
      this._userPair.reset();
      this._reading.setLength(0);
   }

   public void setFuzzyPins(int[] info) {
      int offset = 0;
      this._pinsCount = info[0];

      for (int i = 0; i < this._pinsCount; i++) {
         this._pins[i] = (char)info[i + 1];
         offset += info[i + 1] >> 16;
         this._pinsOffsets[i] = offset;
      }
   }

   public char getCorrespondingPinId() {
      char id = '\uffff';
      if (this._pinsCount > 0
         && (super._additionalVariants._count <= 0 || super._currentVariantIndex >= super._additionalVariants._count)
         && this._pins[0] != '\uffff') {
         int index = super._additionalVariants._count > 0 && super._currentVariantIndex > super._additionalVariants._count
            ? super._currentVariantIndex - super._additionalVariants._count
            : super._currentVariantIndex;

         for (int i = 0; i < this._pinsCount; i++) {
            if (index < this._pinsOffsets[i]) {
               return this._pins[i];
            }
         }
      }

      return id;
   }

   @Override
   public void separateVariants() {
      if (super._variants._count > 0 || super._additionalVariants._count > 0) {
         super._isVariantsSeparated = true;
      }
   }

   @Override
   public int getOverlapLengthFor(int variantPosition) {
      return this._variantsStartIndexes != null && this.getAdditionalVariantsCount() > 0 && variantPosition < this.getAdditionalVariantsCount()
         ? this._variantsStartIndexes[variantPosition]
         : super.getOverlapLengthFor(variantPosition);
   }

   @Override
   public void higlightCurrentVariant(boolean anEnable) {
      super._showSelected = anEnable;
   }

   public void getCurrentVariantTones(byte[] result, int fromIndex) {
      int offset = Arrays.sum(super._additionalVariants._lengths, 0, super._currentVariantIndex, false);
      System.arraycopy(this._wordsTones, offset, result, fromIndex, this.currentVariantLength());
   }

   @Override
   public void setVariants(SLTextDataContainer aData) {
      super.setVariants(aData);
      this._pinsCount = 0;
   }

   @Override
   public void setVariants(char[] words, byte[] lengths, int count) {
      super.setVariants(words, lengths, count);
      this._pinsCount = 0;
   }

   public void setAdditionalArrays(char[] words, byte[] lengths, byte[] startIndexes, byte[] wordsTones, int count) {
      this._variantsStartIndexes = startIndexes;
      int varIndex = super._currentVariantIndex;
      this._wordsTones = wordsTones;
      if (words == null) {
         this._isFixedVariantsSet = false;
         if (super._currentVariantIndex >= super._additionalVariants._count) {
            varIndex = super._currentVariantIndex - super._additionalVariants._count;
         }
      }

      if (this._cachedVariantIndex >= super._additionalVariants._count) {
         if (words == null) {
            this._cachedVariantIndex = this._cachedVariantIndex - super._additionalVariants._count;
         } else {
            this._cachedVariantIndex = this._cachedVariantIndex + (count - super._additionalVariants._count);
         }
      }

      super.setAdditionalArrays(words, lengths, count);
      if (varIndex != super._currentVariantIndex) {
         this.setVariantIndex(varIndex);
      }
   }

   @Override
   public void setAdditionalArrays(SLTextDataContainer aData) {
      super.setAdditionalArrays(aData);
      if (aData == null || aData._words == null) {
         this._isFixedVariantsSet = false;
      }
   }

   public int getRemovedIndex() {
      return this._removedIndex;
   }

   public void restoreRemovedVariant(boolean removeFromAdditional) {
      char[] words = super._variants._words;
      byte[] lengths = super._variants._lengths;
      int count = super._variants._count;
      System.arraycopy(words, this._removedIndex, words, this._removedIndex + 1, count - this._removedIndex);
      System.arraycopy(lengths, this._removedIndex, lengths, this._removedIndex + 1, count - this._removedIndex);
      lengths[this._removedIndex] = 1;
      words[this._removedIndex] = this._removedVariant;
      super._variants.init(words, lengths, ++count);
      if (removeFromAdditional && super._additionalVariants._count > 0) {
         words = super._additionalVariants._words;
         lengths = super._additionalVariants._lengths;
         if (lengths[0] == 1 && words[0] == this._removedVariant) {
            if (super._currentVariantIndex > 0) {
               super._currentVariantIndex--;
            }

            count = super._additionalVariants._count - 1;
            if (count > 0) {
               int sum = Arrays.sum(lengths, 1, count, false);
               System.arraycopy(words, 1, words, 0, sum);
               System.arraycopy(lengths, 1, lengths, 0, count);
               System.arraycopy(this._wordsTones, 1, this._wordsTones, 0, sum);
               super._additionalVariants.init(words, lengths, count);
               super._additionalVariants.seek(super._currentVariantIndex);
            } else {
               super._additionalVariants.init(null, null, 0);
               this._wordsTones = null;
            }
         }
      }

      this._removedIndex = -1;
   }

   public void removeVariantAt(int index) {
      char[] words = super._variants._words;
      byte[] lengths = super._variants._lengths;
      this._removedVariant = words[index];
      int count = super._variants._count - 1;
      System.arraycopy(words, index + 1, words, index, count - index);
      System.arraycopy(lengths, index + 1, lengths, index, count - index);
      super._variants.init(words, lengths, count);
      this._removedIndex = index;
   }

   public boolean isCandidateChanged() {
      if (this._segmentLength == 1 && super._currentVariantIndex != -1 && !super._isVariantsSeparated && this._originalCandidate != '\uffff') {
         _candBuffer.setLength(0);
         super._variants.addCurrentTo(_candBuffer);
         return _candBuffer.charAt(0) != this._originalCandidate;
      } else {
         return false;
      }
   }

   public void setOriginalCandidate(char original) {
      this._originalCandidate = original;
   }

   public char getOriginalCandidate() {
      return this._originalCandidate;
   }

   public void setOriginalCandidate() {
      if (this.currentVariantLength() > 1) {
         this._originalCandidate = 0;
      } else {
         _candBuffer.setLength(0);
         if (this._removedIndex != -1) {
            this._originalCandidate = this._removedVariant;
         } else {
            super._variants.addCurrentTo(_candBuffer);
            this._originalCandidate = _candBuffer.charAt(0);
         }
      }
   }

   public int getVariantLengthAt(int anIndex) {
      int ret = 0;
      if (anIndex >= 0 && anIndex < super._variants._count + super._additionalVariants._count) {
         if (super._additionalVariants._count > 0 && anIndex < super._additionalVariants._count) {
            return super._additionalVariants._lengths[anIndex];
         }

         ret = super._variants._lengths[anIndex - super._additionalVariants._count];
      }

      return ret;
   }

   public int setAsCurrent(char toSet) {
      if (super._additionalVariants._count > 0 && super._additionalVariants._lengths[0] == 1 && super._additionalVariants._words[0] == toSet) {
         return 0;
      }

      int index = this.getIndexOf(toSet);
      if (index != -1) {
         super._currentVariantIndex = index;
         super._variants.seek(index);
      }

      return index;
   }

   public int getIndexOf(char variant) {
      int ret = -1;

      for (int i = 0; i < super._variants._count; i++) {
         if (super._variants._words[i] == variant) {
            return i + super._additionalVariants._count;
         }
      }

      return ret;
   }

   public void setFixedVariantsFlag(boolean value) {
      this._isFixedVariantsSet = value;
   }

   public boolean isFixedVariantsSet() {
      return this._isFixedVariantsSet;
   }

   public void setCachedVariantIndex(int index) {
      this._cachedVariantIndex = index;
   }

   public int getCachedVariantIndex() {
      return this._cachedVariantIndex;
   }

   public void setIndexInSegment(int index) {
      this._indexInSegment = (byte)index;
   }

   public byte getIndexInSegment() {
      return this._indexInSegment;
   }

   public byte getCachedIndexInSegment() {
      return this._cachedIndexInSegment;
   }

   public void setSegmentLength(int length) {
      this._segmentLength = (byte)length;
   }

   public byte getSegmentLength() {
      return this._segmentLength;
   }

   public byte getCachedSegmentLength() {
      return this._cachedSegmentLength;
   }
}
