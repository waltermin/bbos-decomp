package net.rim.tid.im.conv.repository;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.Arrays;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.im.util.algorithm.IntArraySortParallelByte;
import net.rim.tid.util.SLTextDataContainer;
import net.rim.vm.Array;

public class ResultContainer implements PersistentContentListener {
   protected char[] _words;
   protected int _totalLength;
   protected byte[] _lengths;
   protected short[] _offsets;
   protected boolean _offsetsComputed;
   protected int[] _frequencies;
   private int[] _frequenciesSortTmp;
   private long _acceptibleRepositoryTypes;
   private IntArraySortParallelByte sorter = new IntArraySortParallelByte();
   protected byte[] _sources;
   protected byte _currSource;
   protected byte[] _order;
   protected int _variantsCount;
   protected int[] _hashCodes;
   protected boolean[] _toDelete;
   protected boolean _hasDeleteMarks;
   protected Object[] _userObjects;
   protected int _capacity;
   protected boolean _isPredictive = true;
   protected boolean _isWordToResult = true;
   protected int _minWordLength = 0;
   protected int _maxWordLength = Integer.MAX_VALUE;
   protected int _minFrequency = Integer.MAX_VALUE;
   private SLTextDataContainer _dataContainer = new SLTextDataContainer();
   private SLTextDataContainer _additionalDataContainer = new SLTextDataContainer();
   protected ExtendedCurrentVariant _insertedWord = new ExtendedCurrentVariant();
   protected SLCurrentVariant _suffix;
   private boolean _isValidOrder = true;
   private boolean _hasCaseCorrected;
   protected ResultContainer$FrequencyModifier _frequencyModifier;
   private char[] _nullWords = new char[0];
   private byte[] _nullLengths = new byte[0];
   public static final byte SRC_GENERIC = 0;
   public static final byte SRC_EXTENDED_GENERIC_DEFAULT = 1;
   public static final byte LAST_FOR_COMPOUNDS_ID = 1;
   public static final byte SRC_GENERIC_SYNTHETIC = 2;
   public static final byte SRC_LEARNING = 3;
   public static final byte SRC_GENERIC_SUPPLEMENTARY = 4;
   public static final byte SRC_FREQLEARNING = 5;
   public static final byte SRC_PAIRLEARNING = 6;
   public static final byte SRC_AUTOTEXT = 7;
   public static final byte SRC_ADDRESSBOOK = 8;
   public static final byte SRC_MAIL_EXTRACTOR = 9;
   public static final byte LAST_REGULAR_SOURCE_ID = 9;
   public static final byte SRC_GENERIC_COMPOUND = 10;
   public static final byte SRC_NGRAM = 11;
   public static final byte SRC_SYNTHETIC = 12;
   public static final byte SRC_COMMA_SYNTHETIC = 13;
   public static final byte SRC_SPELL_CHECK = 14;
   public static final byte LAST_SOURCE_ID = 14;

   public void setFrequencyModifier(ResultContainer$FrequencyModifier modifier) {
      this._frequencyModifier = modifier;
   }

   public void setAcceptibleRepositoryTypes(long repositoryTypes) {
      this._acceptibleRepositoryTypes = repositoryTypes;
   }

   public void reset(int aSize, int aMinWordLength, int aMaxWordLength, boolean aIsWordToResult, boolean aIsPredictive) {
      this._isPredictive = aIsPredictive;
      this._isWordToResult = aIsWordToResult;
      this._minWordLength = aMinWordLength;
      this._maxWordLength = aMaxWordLength;
      this._acceptibleRepositoryTypes = -1;
      this.ensureCapacity(aSize);
      this.resetResults(aSize);
   }

   public void reset(int aSize) {
      this.ensureCapacity(aSize);
      this.resetResults(aSize);
   }

   public void reset() {
      this.resetResults(this._frequencies.length);
   }

   protected void ensureCapacity(int aSize) {
      if (this._words == null) {
         this._words = new char[aSize * 6];
      }

      this._capacity = aSize;
      if (this._frequencies == null) {
         this._frequencies = new int[aSize];
         this._sources = new byte[aSize];
         this._hashCodes = new int[aSize];
         this._toDelete = new boolean[aSize];
         this._lengths = new byte[aSize];
         this._order = new byte[aSize];
         this._offsets = new short[aSize];
         this._userObjects = new Object[aSize];
         this.createSpecificData(aSize);
      } else {
         if (this._frequencies.length < aSize) {
            Array.resize(this._frequencies, aSize);
            Array.resize(this._sources, aSize);
            Array.resize(this._hashCodes, aSize);
            Array.resize(this._toDelete, aSize);
            Array.resize(this._lengths, aSize);
            Array.resize(this._order, aSize);
            Array.resize(this._offsets, aSize);
            Array.resize(this._userObjects, aSize);
            this.createSpecificData(aSize);
         }
      }
   }

   protected void createSpecificData(int aSize) {
   }

   protected void ensureWordBufferCapacity(int aLen) {
      int dif = this._totalLength + aLen - this._words.length;
      if (dif > 0) {
         Array.resize(this._words, this._words.length + Math.max(10, dif));
      }
   }

   protected void resetResults(int aSize) {
      this._variantsCount = 0;
      this._totalLength = 0;
      this._minFrequency = Integer.MAX_VALUE;
      this._offsetsComputed = false;
      this.setCaseCorrectionIndicator(false);
   }

   public void markForDelete(int index) {
      if (index >= 0 && index < this._variantsCount) {
         this._toDelete[index] = true;
         this._hasDeleteMarks = true;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void processDelete() {
      for (int i = this._variantsCount - 1; i >= 0; i--) {
         if (this._toDelete[i]) {
            this.removeWordAt(i);
            this._toDelete[i] = false;
         }
      }

      this._hasDeleteMarks = false;
   }

   public boolean hasDeleteMarks() {
      return this._hasDeleteMarks;
   }

   public void setData(char[] aWords, byte[] aLengths, int aVariantsCount) {
      this.setData(aWords, aLengths, null, null, null, aVariantsCount);
   }

   public void setData(SLTextDataContainer aCont) {
      this.reset();
      aCont.resetIteration();

      while (aCont.hasMoreElements()) {
         aCont.next(this._insertedWord);
         this.insertWord(this._insertedWord);
      }
   }

   public void setData(char[] aWords, byte[] aLengths, int[] aFreq, byte[] aSources, int aVariantsCount) {
      this.setData(aWords, aLengths, aFreq, aSources, null, aVariantsCount);
   }

   public void setData(char[] aWords, byte[] aLengths, int[] aFreq, byte[] aSources, byte[] aOrder, int aVariantsCount) {
      this.ensureCapacity(aVariantsCount);
      this._words = Arrays.copy(aWords);
      this._lengths = Arrays.copy(aLengths);
      if (aFreq != null) {
         this._frequencies = Arrays.copy(aFreq);
         this._minFrequency = Integer.MAX_VALUE;

         for (int i = 0; i < aVariantsCount; i++) {
            this._minFrequency = Math.min(this._minFrequency, this._frequencies[i]);
         }
      } else if (this._frequencies.length < aVariantsCount) {
         this._frequencies = new int[aVariantsCount];
      }

      if (aSources != null) {
         this._sources = Arrays.copy(aSources);
      } else if (this._sources.length < aVariantsCount) {
         this._sources = new byte[aVariantsCount];
      }

      this._variantsCount = aVariantsCount;
      if (aOrder != null) {
         this._order = Arrays.copy(aOrder);
      } else {
         if (this._order.length < aVariantsCount) {
            this._order = new byte[aVariantsCount];
         }

         for (int i = 0; i < aVariantsCount; i++) {
            this._order[i] = (byte)i;
         }
      }

      if (this._userObjects.length < aVariantsCount) {
         this._userObjects = new Object[aVariantsCount];
      }

      this.calcHashCodesAndLen();
      this.computeOffsets();
   }

   public final void calcHashCodesAndLen() {
      SLCurrentVariant variant = this._insertedWord;
      variant._variants = this._words;
      this._totalLength = 0;

      for (int i = 0; i < this._variantsCount; i++) {
         variant._offset = this._totalLength;
         variant._length = this._lengths[i];
         this._hashCodes[i] = variant.hashCode();
         this._totalLength = this._totalLength + this._lengths[i];
      }
   }

   public void setCurrentSource(byte aSource) {
      this.setCurrentSource(aSource, true);
   }

   public void setCurrentSource(byte aSource, boolean aStartNewWordlist) {
      if (aStartNewWordlist) {
         this.newWordlist();
      }

      this._currSource = aSource;
   }

   public byte getCurrentSource() {
      return this._currSource;
   }

   public synchronized void addFrequencyModifier(int index, int modifier) {
      if (index >= this._variantsCount) {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      this._frequencies[this._order[index]] = this._frequencies[this._order[index]] | modifier;
      this._isValidOrder = false;
   }

   public synchronized void addFrequencyModifierIgnoreOrder(int index, int modifier) {
      if (index >= this._variantsCount) {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      this._frequencies[index] = this._frequencies[index] | modifier;
      this._isValidOrder = false;
   }

   public synchronized void setVariantAt(int index, SLCurrentVariant variant) {
      if (index >= this._variantsCount) {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[index];
      if (this._lengths[idx] != variant._length) {
         throw new IllegalArgumentException("" + this._lengths[idx] + " != " + variant._length);
      }

      System.arraycopy(variant._variants, variant._offset, this._words, this._offsets[idx], this._lengths[idx]);
   }

   public synchronized void removeFrequencyModifier(int index, int modifier) {
      if (index >= this._variantsCount) {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      this._frequencies[this._order[index]] = this._frequencies[this._order[index]] & ~modifier;
      this._isValidOrder = false;
   }

   public synchronized void resort() {
      this.resort(-65536);
   }

   public synchronized void resort(int frequencyModifiers) {
      this.initTempResortingContainer();
      byte i = (byte)0;

      while (i < this._variantsCount) {
         this._order[i] = i++;
      }

      for (int ix = 0; ix < this._frequenciesSortTmp.length; ix++) {
         this._frequenciesSortTmp[ix] = this._frequenciesSortTmp[ix] & (65535 | frequencyModifiers);
      }

      this.sorter.sort(this._frequenciesSortTmp, 0, this._variantsCount, this._order);
      i = (byte)0;

      for (int j = this._variantsCount - 1; i < j; j--) {
         byte temp = this._order[i];
         this._order[i] = this._order[j];
         this._order[j] = temp;
         i++;
      }

      this._isValidOrder = true;
   }

   public boolean isValidOrder() {
      return this._isValidOrder;
   }

   public int getVariantsCount() {
      return this._variantsCount;
   }

   public void getVariantAt(int aPos, ExtendedCurrentVariant aVariant) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[aPos];
      aVariant.set(this._words, this._offsets[idx], this._lengths[idx]);
      aVariant._frequency = this._frequencies[idx];
      aVariant._source = this._sources[idx];
      aVariant._userObject = this._userObjects[idx];
      this.setSpecificData(aPos, aVariant, false);
   }

   public void getVariantAt(int aPos, SLCurrentVariant aVariant) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[aPos];
      aVariant.set(this._words, this._offsets[idx], this._lengths[idx]);
   }

   public void getVariantAt(int aPos, StringBuffer buf) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[aPos];
      buf.append(this._words, this._offsets[idx], this._lengths[idx]);
   }

   protected int getWordFrequencyAt(int aPos) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[aPos];
      return this._frequencies[idx];
   }

   public void setWordFrequencyAt(int aPos, int newFrequency) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[aPos];
      this._frequencies[idx] = newFrequency;
   }

   public void getRefToVariantAt(int aPos, ExtendedCurrentVariant aVariant) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int idx = this._order[aPos];
      aVariant.setData(this._words, this._offsets[idx], this._lengths[idx], this._frequencies[idx]);
      aVariant._source = this._sources[idx];
      aVariant._userObject = this._userObjects[idx];
      this.setSpecificData(aPos, aVariant, false);
   }

   protected void setSpecificData(int aPos, ExtendedCurrentVariant aVariant, boolean ignoreOrder) {
   }

   public char[] getWords() {
      return this._words;
   }

   public byte[] getLengths() {
      return this._lengths;
   }

   public int[] getFrequencies() {
      return this._frequencies;
   }

   public byte[] getSources() {
      return this._sources;
   }

   public int[] getWordsHashCodes() {
      return this._hashCodes;
   }

   public byte[] getOrder() {
      return this._order;
   }

   public void setOrder(byte[] aOrder) {
      this._order = aOrder;
   }

   public int getTotalLength() {
      return this._totalLength;
   }

   public short[] getOffsets() {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      return this._offsets;
   }

   public int getCapacity() {
      return this._capacity;
   }

   public void setCapacity(int aCapacity) {
      this.ensureCapacity(aCapacity);
   }

   public int getMaxWordLength() {
      return this._maxWordLength;
   }

   public void setMaxWordLength(int aMaxWordLength) {
      this._maxWordLength = aMaxWordLength;
   }

   public int getMinWordLength() {
      return this._minWordLength;
   }

   public boolean isPredictive() {
      return this._isPredictive;
   }

   public boolean isFast() {
      return false;
   }

   public boolean isSpellCheck() {
      return false;
   }

   public void setMinWordLength(int aMinWordLength) {
      this._minWordLength = aMinWordLength;
   }

   public int getPrefixNo() {
      return 0;
   }

   public char[] getPrefixes() {
      return null;
   }

   public byte[] getPrefixSources() {
      return null;
   }

   public void setPrefixes(char[] aPrefixes, int aPrefixLen, int aPrefixNo) {
   }

   public void setResults(SLVariants aVariant) {
      this.setResults(aVariant, this._variantsCount);
   }

   public void setResults(SLVariants aVariant, int aMaxVariantCount) {
      this._additionalDataContainer.init(this._nullWords, this._nullLengths, 0);
      aVariant.setAdditionalArrays(this._additionalDataContainer);
      this.setResults(aVariant, aMaxVariantCount, false);
   }

   public void setResults(SLVariants aVariant, int aMaxVariantCount, boolean aIsAdditionalArray) {
      SLTextDataContainer container;
      if (aIsAdditionalArray) {
         container = this._additionalDataContainer;
         aVariant.getAdditionalVariants(container);
         if (container._lengths == null) {
            container.init(this._nullWords, this._nullLengths, 0);
         }
      } else {
         container = this._dataContainer;
         aVariant.getVariants(container);
      }

      int count = Math.min(aMaxVariantCount, this._variantsCount);
      container._count = count;
      if (count == 0) {
         if (aIsAdditionalArray) {
            aVariant.setAdditionalArrays(container);
         } else {
            aVariant.setVariants(container);
         }
      } else {
         if (count > container._lengths.length) {
            Array.resize(container._lengths, count);
         }

         if (this._totalLength > container._words.length) {
            Array.resize(container._words, this._totalLength);
         }

         if (!this._offsetsComputed) {
            this.computeOffsets();
         }

         int newStart = 0;

         for (int i = 0; i < count; i++) {
            int idx = this._order[i];
            byte length = this._lengths[idx];
            container._lengths[i] = length;
            System.arraycopy(this._words, this._offsets[idx], container._words, newStart, length);
            newStart += length;
         }

         if (aIsAdditionalArray) {
            aVariant.setAdditionalArrays(container);
         } else {
            aVariant.setVariants(container);
            int additionalVariantsCount = aVariant.getAdditionalVariantsCount();

            for (int i = 0; i < count; i++) {
               int idx = this._order[i];
               aVariant.setVariantSource(additionalVariantsCount + i, this._sources[idx]);
            }

            aVariant.setVariantIndex(-1);
         }
      }
   }

   protected void computeOffsets() {
      short offset = 0;

      for (int i = 1; i < this._variantsCount; i++) {
         offset = (short)(offset + this._lengths[i - 1]);
         this._offsets[i] = offset;
      }

      this._offsetsComputed = true;
   }

   public int getMinFrequency() {
      return this._minFrequency;
   }

   public int getOriginalFrequency(ExtendedCurrentVariant aVariant) {
      return aVariant._frequency & 65535;
   }

   public boolean insert(ExtendedCurrentVariant aWord) {
      return this.insert(aWord, 0, null, 1);
   }

   public boolean insert(ExtendedCurrentVariant aWord, int aPrefixLength) {
      return this.insert(aWord, aPrefixLength, null, 1);
   }

   public boolean insert(ExtendedCurrentVariant aWord, int aPrefixLength, ReIterator aPrefix, int aCount) {
      return !this.isInsertable(aWord, aPrefixLength) ? false : this.insertWord(aWord) != -1;
   }

   public int insertWord(ExtendedCurrentVariant aWord) {
      if (this._variantsCount == this._capacity && aWord._frequency <= this._minFrequency) {
         return -1;
      }

      if (this._isPredictive && this._currSource > 9) {
         int prefixLen = aWord._length;

         for (int i = 0; i < this._variantsCount; i++) {
            if (this.prefixesEqual(i, aWord, prefixLen)) {
               return -1;
            }
         }
      } else {
         int code = aWord.hashCode();

         for (int i = 0; i < this._variantsCount; i++) {
            if (this._hashCodes[i] == code && this.wordsEqual(i, aWord)) {
               if (aWord._frequency > this._frequencies[i]) {
                  this.reorder(i, aWord._frequency);
                  this._sources[i] = this._currSource;
               }

               return -(i + 2);
            }
         }
      }

      if (aWord._frequency <= this._minFrequency && this._variantsCount < this._capacity) {
         int place = this._variantsCount;
         int code = aWord.hashCode();
         this._variantsCount++;
         this.addWord(aWord, place, place, this._totalLength, code);
         return place;
      }

      int freq = aWord._frequency;

      for (int i = 0; i < this._variantsCount; i++) {
         if (this._frequencies[this._order[i]] < freq) {
            return this.shiftAndInsert(aWord, i);
         }
      }

      return -1;
   }

   protected int shiftAndInsert(ExtendedCurrentVariant aWord, int aPosition) {
      int count;
      int place;
      int from;
      if (this._variantsCount < this._capacity) {
         count = this._variantsCount - aPosition;
         place = this._variantsCount++;
         from = this._totalLength;
      } else {
         count = this._variantsCount - 1 - aPosition;
         place = this._order[this._variantsCount - 1];
         if (place < this._variantsCount / 2) {
            from = 0;

            for (int i = 0; i < place; i++) {
               from += this._lengths[i];
            }
         } else {
            from = this._totalLength;

            for (int i = this._variantsCount - 1; i >= place; i--) {
               from -= this._lengths[i];
            }
         }

         this._totalLength = this._totalLength - this._lengths[place];
         if (aWord._length != this._lengths[place]) {
            this.ensureWordBufferCapacity(aWord._length);
            System.arraycopy(this._words, from + this._lengths[place], this._words, from + aWord._length, this._totalLength - from);
         }
      }

      if (aPosition < this._capacity - 1) {
         System.arraycopy(this._order, aPosition, this._order, aPosition + 1, count);
      }

      int code = aWord.hashCode();
      this.addWord(aWord, aPosition, place, from, code);
      return place;
   }

   protected void addWord(ExtendedCurrentVariant aWord, int aOrderId, int aPlaceId, int aDataShift, int aHashCode) {
      this._order[aOrderId] = (byte)aPlaceId;
      int len = aWord._length;
      this.ensureWordBufferCapacity(len);
      System.arraycopy(aWord._variants, aWord._offset, this._words, aDataShift, len);
      this._totalLength += len;
      this._lengths[aPlaceId] = (byte)len;
      this._frequencies[aPlaceId] = aWord._frequency;
      this._sources[aPlaceId] = this._currSource;
      this._hashCodes[aPlaceId] = aHashCode;
      this._userObjects[aPlaceId] = aWord._userObject;
      this._minFrequency = this._frequencies[this._order[this._variantsCount - 1]];
      this._offsetsComputed = false;
   }

   protected void reorder(int aPlace, int aNewFreq) {
      int old_order = -1;

      for (int j = 0; j < this._variantsCount; j++) {
         if (this._order[j] == aPlace) {
            old_order = j;
            break;
         }
      }

      if (old_order == -1) {
         throw new IllegalStateException("");
      }

      int new_order = old_order;

      for (int j = 0; j < old_order; j++) {
         if (this._frequencies[this._order[j]] < aNewFreq) {
            new_order = j;
            break;
         }
      }

      if (old_order > new_order) {
         System.arraycopy(this._order, new_order, this._order, new_order + 1, old_order - new_order);
         this._order[new_order] = (byte)aPlace;
      }

      this._frequencies[aPlace] = aNewFreq;
      this._minFrequency = this._frequencies[this._order[this._variantsCount - 1]];
   }

   public void swapOrder(int aIndex1, int aIndex2) {
      byte temp = this._order[aIndex1];
      this._order[aIndex1] = this._order[aIndex2];
      this._order[aIndex2] = temp;
   }

   public void removeWordAt(int aId) {
      this.removeWordAtDirectIndex(this._order[aId]);
   }

   public void removeWordAtDirectIndex(int aId) {
      if (!this._offsetsComputed) {
         this.computeOffsets();
      }

      int offset = this._offsets[aId];
      int from = offset + this._lengths[aId];
      System.arraycopy(this._words, from, this._words, offset, this._totalLength - from);
      this._totalLength = this._totalLength - this._lengths[aId];
      from = aId + 1;
      System.arraycopy(this._lengths, from, this._lengths, aId, this._variantsCount - from);
      System.arraycopy(this._frequencies, from, this._frequencies, aId, this._variantsCount - from);
      System.arraycopy(this._sources, from, this._sources, aId, this._variantsCount - from);
      System.arraycopy(this._userObjects, from, this._userObjects, aId, this._variantsCount - from);
      System.arraycopy(this._hashCodes, from, this._hashCodes, aId, this._variantsCount - from);
      this.computeOffsets();
      this.removeSpecificData(aId);
      this._variantsCount--;
      this._minFrequency = this._variantsCount == 0 ? 65535 : this._frequencies[this._order[this._variantsCount - 1]];
      boolean shift = false;

      for (int i = 0; i < this._variantsCount; i++) {
         if (shift || this._order[i] == aId) {
            this._order[i] = this._order[i + 1];
            shift = true;
         }

         if (this._order[i] > aId) {
            this._order[i]--;
         }
      }
   }

   protected void removeSpecificData(int aId) {
   }

   public boolean isFull() {
      return this._variantsCount == this._capacity;
   }

   public ExtendedCurrentVariant getTempInsertedWordContainer() {
      return this._insertedWord;
   }

   public void setSuffix(SLCurrentVariant aSuffix) {
      this._suffix = aSuffix;
   }

   public SLCurrentVariant getSuffix() {
      return this._suffix;
   }

   public boolean isWordToResult() {
      return this._isWordToResult;
   }

   public boolean hasCaseCorrected() {
      return this._hasCaseCorrected;
   }

   public void setCaseCorrectionIndicator(boolean isOn) {
      this._hasCaseCorrected = isOn;
   }

   public void appendVariants(ResultContainer aRes) {
      this.newWordlist();
      int freq = Math.max(0, this._minFrequency - 1);
      int count = aRes.getVariantsCount();

      for (int i = 0; i < count && !this.isFull(); i++) {
         int oldCount = this._variantsCount;
         aRes.getVariantAt(i, this._insertedWord);
         this._insertedWord._frequency = freq;
         this.insert(this._insertedWord, 0, null, 1);
         if (oldCount != this._variantsCount) {
            freq = Math.max(freq - 1, 0);
         }
      }
   }

   public boolean insertFast(ExtendedCurrentVariant aWord, ReIterator aPrefix, int aCount) {
      return false;
   }

   public int insertValidPrefix(ExtendedCurrentVariant aWord, ReIterator aPrefix, int aCount) {
      return -1;
   }

   public void resetWildcardSubst(int aNo) {
   }

   public void insertRestOfRegularFastVariants(int aSubstNo) {
   }

   public void newWordlist() {
   }

   public byte[] getWildcardSubstCount() {
      return null;
   }

   public boolean containsPrefix(SLCurrentVariant aPrefix) {
      return false;
   }

   public boolean containsWord(SLCurrentVariant aWord) {
      return false;
   }

   public void insertNgramPrefixes() {
   }

   protected boolean wordsEqual(int aIndex, ExtendedCurrentVariant aWord) {
      return this._lengths[aIndex] != aWord._length ? false : this.prefixesEqual(aIndex, aWord, aWord._length);
   }

   protected boolean prefixesEqual(int aIndex, ExtendedCurrentVariant aWord, int prefixLen) {
      int start = 0;

      for (int i = 0; i < aIndex; i++) {
         start += this._lengths[i];
      }

      for (int i = 0; i < prefixLen; i++) {
         if (aWord._variants[aWord._offset + i] != this._words[start + i]) {
            return false;
         }
      }

      return true;
   }

   public long getAcceptibleRepositoryTypes() {
      return this._acceptibleRepositoryTypes;
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2 && this._words != null) {
         Arrays.zero(this._words);
      }
   }

   private void initTempResortingContainer() {
      if (this._frequenciesSortTmp == null) {
         this._frequenciesSortTmp = new int[this._frequencies.length];
      } else if (this._frequenciesSortTmp.length < this._frequencies.length) {
         Array.resize(this._frequenciesSortTmp, this._frequencies.length);
      }

      System.arraycopy(this._frequencies, 0, this._frequenciesSortTmp, 0, this._variantsCount);
   }

   public ResultContainer(int aSize, int aMinWordLength, int aMaxWordLength, boolean aIsWordToResult, boolean aIsPredictive) {
      this();
      this.reset(aSize, aMinWordLength, aMaxWordLength, aIsWordToResult, aIsPredictive);
   }

   private boolean isInsertable(ExtendedCurrentVariant aWord, int aPrefixLength) {
      int len = aWord._length;
      if (len < this._minWordLength || len > this._maxWordLength) {
         return false;
      } else {
         return !this._isWordToResult && aPrefixLength == len ? false : this._variantsCount != this._capacity || aWord._frequency > this._minFrequency;
      }
   }

   public ResultContainer(int aSize) {
      this();
      this.ensureCapacity(aSize);
   }

   public ResultContainer() {
      PersistentContent.addWeakListener(this);
   }
}
