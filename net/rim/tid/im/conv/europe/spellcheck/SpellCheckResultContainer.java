package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.AccentGrouping;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.europe.repository.KeypadLayout;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;

public class SpellCheckResultContainer extends ResultContainer {
   private char[] _origWord = new char[20];
   private int _origLength;
   private byte _conversionFunction;
   public byte[] _distance;
   public byte[] _subDistance;
   private char[] _metaphoneKey = new char[10];
   private int _metaphoneLen;
   private SLCurrentVariant _metaphoneKey1 = new SLCurrentVariant();
   private short[] _distTable = new short[100];
   private int _weightJump;
   private int _weightIncrease;
   private LocaleConversionRules _localeRules;
   private int _elementsBeforeRule;
   private int _learnedElements;
   private int _checkedLearnedElements;
   private byte _lastDistance;
   private byte _firstDistance;
   private boolean _searchingLearningDB;
   private boolean _capitalize;
   private boolean _allUpperCase;
   private Locale _locale;
   private Word word;
   private Word lower;
   private Word accentless;
   private Word _tempWord = new Word();
   private ExtendedCurrentVariant _tempVariant = new ExtendedCurrentVariant();
   private char[] _tempBuf = new char[50];
   public static final byte LEARNED_WORDS_FUNCTION = 1;
   public static final byte LEARNED_WORDS_FUNCTION_WEIGHT = 1;
   public static final byte CASE_ACCENT_INSENSITIVE_FUNCTION = 2;
   public static final byte CASE_ACCENT_INSENSITIVE_FUNCTION_WEIGHT = 2;
   public static final byte PHONETIC_REPLACE_FUNCTION = 3;
   public static final byte PHONETIC_REPLACE_FUNCTION_WEIGHT = 3;
   public static final byte MISSING_CHAR_FUNCTION = 4;
   public static final byte MISSING_CHAR_FUNCTION_WEIGHT = 3;
   public static final byte SWAP_CHAR_FUNCTION = 5;
   public static final byte SWAP_CHAR_FUNCTION_WEIGHT = 3;
   public static final byte EXTRA_CHAR_FUNCTION = 6;
   public static final byte EXTRA_CHAR_FUNCTION_WEIGHT = 3;
   public static final byte EXCHANGE_CHAR_FUNCTION = 7;
   public static final byte EXCHANGE_CHAR_FUNCTION_WEIGHT = 3;
   public static final byte SPLIT_WORDS_FUNCTION = 8;
   public static final byte SPLIT_WORDS_FUNCTION_WEIGHT = 3;
   public static final byte SAME_METAPHONE_FUNCTION = 9;
   public static final byte SAME_METAPHONE_FUNCTION_WEIGHT = 9;
   public static final byte MISSING_METAPHONE_FUNCTION = 10;
   public static final byte MISSING_METAPHONE_FUNCTION_WEIGHT = 10;
   public static final byte SWAP_METAPHONE_FUNCTION = 11;
   public static final byte SWAP_METAPHONE_FUNCTION_WEIGHT = 11;
   public static final byte EXTRA_METAPHONE_FUNCTION = 12;
   public static final byte EXTRA_METAPHONE_FUNCTION_WEIGHT = 12;
   public static final byte EXCHANGE_METAPHONE_FUNCTION = 13;
   public static final byte EXCHANGE_METAPHONE_FUNCTION_WEIGHT = 13;
   public static final byte GREATEST_COMMON_PREFIX_FUNCTION = 14;
   public static final byte GREATEST_COMMON_PREFIX_FUNCTION_WEIGHT = 14;
   public static final byte TOTAL_CONVERSION_FUNCTIONS = 14;
   public static final byte[] _conversionFunctionWeights = new byte[14];
   public static final byte LAST_REGULAR_SEARCH_FUNCTION = 7;
   private static final byte MIN_ELEMENTS_FOR_JUMP = 1;
   private static final int CHAR_DIFF_SHIFT = 8;
   private static final int CASE_DIFF_SHIFT = 5;
   private static final int ACCENT_DIFF_SHIFT = 6;
   private static final int SHARED_KEY_SHIFT = 6;
   private static final int ADJACENT_KEY_SHIFT = 7;
   private static final int CHAR_DIFF_COST = 256;
   private static final int CASE_DIFF_COST = 32;
   private static final int ACCENT_DIFF_COST = 64;
   private static final int ADJACENT_KEY_COST = 128;
   private static final int SHARED_KEY_COST = 64;

   public SpellCheckResultContainer(int aSize) {
      super(aSize);
      this._metaphoneKey1._variants = new char[50];
   }

   public void setLocale(Locale locale) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setCapitalize(boolean capitalize) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setAllUpperCase(boolean allUpperCase) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void resetResults(int aSize) {
      super.resetResults(aSize);
      this._localeRules = null;
      this._weightIncrease = 0;
      this._learnedElements = this._checkedLearnedElements = 0;
      this._lastDistance = 0;
      this._firstDistance = 127;
      this._capitalize = false;
      this._allUpperCase = false;
   }

   @Override
   protected void createSpecificData(int aSize) {
      this._distance = new byte[aSize];
      this._subDistance = new byte[aSize];
   }

   public void setOriginalWord(char[] aWord, int aLen, char[] aMetaphoneKey, int aMetaphoneLen, IMetaphone aMetaphone) {
      if (this._origWord.length < aLen) {
         this._origWord = new char[aLen];
      }

      System.arraycopy(aWord, 0, this._origWord, 0, aLen);
      this._origLength = aLen;
      this._metaphoneLen = aMetaphoneLen;
      if (this._metaphoneKey.length < aMetaphoneLen) {
         this._metaphoneKey = new char[aMetaphoneLen];
      }

      if (this._metaphoneLen > 0) {
         System.arraycopy(aMetaphoneKey, 0, this._metaphoneKey, 0, this._metaphoneLen);
      }

      if (this.word == null) {
         this.word = new Word();
         this.accentless = new Word();
      }

      this.word.init(aWord, 0, aLen, this._locale);
      this.lower = this.word.getLower();
      this.lower.toAccentless(this.accentless);
   }

   public void setWeightJump(int aJump) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean setConversionFunction(byte aFunction) {
      if (super._variantsCount < super._capacity && (super._variantsCount < 4 || aFunction < 9)) {
         this._conversionFunction = aFunction;
         return true;
      } else {
         return false;
      }
   }

   public void setLocaleRules(LocaleConversionRules aRules) {
      this._localeRules = aRules;
      if (aRules != null) {
         this._weightIncrease += 12;
         this._elementsBeforeRule = super._variantsCount;
      } else {
         if (this._elementsBeforeRule == super._variantsCount) {
            this._weightIncrease -= 12;
         }
      }
   }

   private boolean isInsertable(ExtendedCurrentVariant aWord) {
      int len = aWord._length;
      return len >= super._minWordLength && len <= super._maxWordLength;
   }

   @Override
   public boolean insert(ExtendedCurrentVariant aWord, int aPrefixLength, ReIterator aPrefix, int aCount) {
      return this.insertWord(aWord) != -1;
   }

   @Override
   public int insertWord(ExtendedCurrentVariant aWord) {
      if (!this.isInsertable(aWord)) {
         return -1;
      }

      if (this._localeRules != null && !this._localeRules.modifyInserted(aWord)) {
         return -1;
      }

      if (this._allUpperCase) {
         if (this._tempBuf.length < aWord._length) {
            Array.resize(this._tempBuf, aWord._length);
         }

         System.arraycopy(aWord._variants, 0, this._tempBuf, 0, aWord._length);

         for (int i = 0; i < aWord._length; i++) {
            this._tempBuf[i] = CaseCorrector.toUpperCase(aWord._variants[i], this._locale);
         }

         this._tempVariant.setData(this._tempBuf, 0, aWord._length, 0);
         this._tempVariant._frequency = aWord._frequency;
         aWord = this._tempVariant;
      } else if (this._capitalize) {
         if (this._tempBuf.length < aWord._length) {
            Array.resize(this._tempBuf, aWord._length);
         }

         System.arraycopy(aWord._variants, 0, this._tempBuf, 0, aWord._length);
         this._tempBuf[0] = CaseCorrector.toUpperCase(aWord._variants[0], this._locale);
         this._tempVariant.setData(this._tempBuf, 0, aWord._length, 0);
         this._tempVariant._frequency = aWord._frequency;
         aWord = this._tempVariant;
      }

      Word other = this._tempWord;
      other.init(aWord._variants, 0, aWord._length, this._locale);
      int distance;
      if (this._conversionFunction == 1) {
         distance = 0;
      } else {
         distance = this.computeEditDistance(aWord, other);
      }

      if (this._conversionFunction > 9) {
         distance++;
      }

      if (this._lastDistance <= 0 || distance - this.getLastDistance() < this._weightJump && distance - this.getFirstDistance() < this._weightJump * 2) {
         if (super._variantsCount != super._capacity
            || distance <= this._distance[super._order[super._variantsCount - 1]]
               && (distance != this._distance[super._order[super._variantsCount - 1]] || aWord._frequency > this.getMinFrequency())) {
            aWord._distance = (byte)distance;
            int code = aWord.hashCode();

            for (int i = 0; i < super._variantsCount; i++) {
               if (super._hashCodes[super._order[i]] == code && aWord._length == super._lengths[super._order[i]]) {
                  if (i < this._learnedElements && !this._searchingLearningDB && this._checkedLearnedElements < this._learnedElements) {
                     this._lastDistance = (byte)Math.max(this._lastDistance, aWord._distance);
                     this._firstDistance = (byte)Math.min(this._firstDistance, aWord._distance);
                     this._checkedLearnedElements++;
                  }

                  return -(i + 2);
               }
            }

            int place = -1;
            if ((super._variantsCount == 0 || this.compareWordToWordAtPosition(aWord, super._order[super._variantsCount - 1]) >= 0)
               && super._variantsCount < super._capacity) {
               place = super._variantsCount++;
               this.addWord(aWord, place, place, super._totalLength, code);
            }

            if (place == -1) {
               for (int i = 0; i < super._variantsCount; i++) {
                  if (this.compareWordToWordAtPosition(aWord, super._order[i]) < 0) {
                     place = this.shiftAndInsert(aWord, i);
                     break;
                  }
               }
            }

            if (place == -1) {
               return place;
            }

            if (this._conversionFunction == 1) {
               this._learnedElements++;
            } else {
               if (super._variantsCount > 1) {
                  this._lastDistance = (byte)Math.max(this._lastDistance, aWord._distance);
               }

               if (place == super._order[this._learnedElements]) {
                  this._firstDistance = (byte)Math.min(this._firstDistance, aWord._distance);
               }
            }

            this._distance[place] = aWord._distance;
            this._subDistance[place] = aWord._subDistance;
            return place;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private int compareWordToWordAtPosition(ExtendedCurrentVariant word, int position) {
      if (word._distance > this._distance[position]) {
         return 1;
      } else if (word._distance < this._distance[position]) {
         return -1;
      } else if (word._subDistance == this._subDistance[position]) {
         return super._frequencies[position] - word._frequency;
      } else if (word._subDistance < this._subDistance[position]) {
         return word._frequency < super._frequencies[position] / 3 ? 1 : -1;
      } else {
         return word._frequency / 3 > super._frequencies[position] ? -1 : 1;
      }
   }

   private byte computeEditDistance(ExtendedCurrentVariant ov, Word other) {
      short dist = this.computeEditDistance(other);
      if ((dist & 255) != 0) {
         ov._subDistance = 0;
         ov._distance = (byte)((dist >> 8) + 1);
      } else {
         ov._distance = (byte)(dist >> 8);
         if (ov._distance == 0) {
            ov._subDistance = 0;
         } else {
            ov._subDistance = 1;
         }
      }

      return ov._distance;
   }

   private short computeEditDistance(Word other) {
      if (this.word == null) {
         return (short)(other.length() << 8);
      }

      Word olower = other.getLower();
      Word oaccentless = olower.toAccentless(null);
      int len1 = this.word.length();
      int len2 = other.length();
      int rowLen = len1 + 1;
      int tableLen = rowLen * (len2 + 1);
      if (this._distTable.length < tableLen) {
         this._distTable = new short[tableLen];
      }

      short[] dt = this._distTable;

      for (int i = 1; i <= len1; i++) {
         dt[i] = (short)(i << 8);
      }

      int row = rowLen;

      for (int i = 1; row < tableLen; i++) {
         dt[row] = (short)(i << 8);
         row += rowLen;
      }

      char[] accentlessText = this.accentless.text;
      int accentlessTextStart = this.accentless.start;
      char[] lowerText = this.lower.text;
      int lowerTextStart = this.lower.start;
      char[] wordText = this.word.text;
      int wordTextStart = this.word.start;
      char[] oaccentlessText = oaccentless.text;
      int oaccentlessTextStart = oaccentless.start;
      char[] olowerText = olower.text;
      int olowerTextStart = olower.start;
      char[] oText = other.text;
      int oTextStart = other.start;

      for (int j = 1; j <= len2; j++) {
         for (int i = 1; i <= len1; i++) {
            char ac1 = accentlessText[accentlessTextStart + i - 1];
            char ac2 = oaccentlessText[oaccentlessTextStart + j - 1];
            int cost;
            if (ac1 == ac2) {
               char lc1 = lowerText[lowerTextStart + i - 1];
               char lc2 = olowerText[olowerTextStart + j - 1];
               if (lc1 == lc2) {
                  char ch1 = wordText[wordTextStart + i - 1];
                  char ch2 = oText[oTextStart + j - 1];
                  if (ch1 == ch2) {
                     cost = 0;
                  } else {
                     cost = 32;
                  }
               } else {
                  cost = 64;
               }
            } else if (KeypadLayout.isAdjacent(ac1, ac2)) {
               cost = 128;
            } else if (KeypadLayout.isShared(ac1, ac2)) {
               cost = 64;
            } else {
               cost = 256;
            }

            int dist = dt[rowLen * (j - 1) + i] + 256;
            int dist2 = dt[rowLen * j + i - 1] + 256;
            if (dist2 < dist) {
               dist = dist2;
            }

            dist2 = dt[rowLen * (j - 1) + i - 1] + cost;
            if (dist2 < dist) {
               dist = dist2;
            }

            if (j > 1 && i > 1 && oaccentlessText[oaccentlessTextStart + j - 2] == ac1 && ac2 == accentlessText[accentlessTextStart + i - 2]) {
               int transCost = dt[rowLen * (j - 2) + i - 2] + cost;
               if (transCost < dist) {
                  dist = transCost;
               }
            }

            dt[j * rowLen + i] = (short)dist;
         }
      }

      return dt[rowLen * len2 + len1];
   }

   private byte computeEditDistance(ExtendedCurrentVariant word) {
      short dist = this.computeEditDistance(this._origWord, this._origLength, word._variants, word._length);
      if ((dist & 255) != 0) {
         word._subDistance = 0;
         word._distance = (byte)((dist >> 8) + 1);
      } else {
         word._distance = (byte)(dist >> 8);
         if (word._distance == 0) {
            word._subDistance = 0;
         } else {
            word._subDistance = 1;
         }
      }

      return word._distance;
   }

   private short computeEditDistance(char[] w1, int len1, char[] w2, int len2) {
      int rowLen = len1 + 1;
      int tableLen = rowLen * (len2 + 1);
      if (this._distTable.length < tableLen) {
         this._distTable = new short[tableLen];
      }

      short[] dt = this._distTable;

      for (int i = 1; i <= len1; i++) {
         dt[i] = (short)(i << 8);
      }

      int row = rowLen;

      for (int i = 1; row < tableLen; i++) {
         dt[row] = (short)(i << 8);
         row += rowLen;
      }

      for (int j = 1; j <= len2; j++) {
         for (int i = 1; i <= len1; i++) {
            char ch1 = w1[i - 1];
            char ch2 = w2[j - 1];
            int cost;
            if (ch1 == ch2) {
               cost = 0;
            } else {
               char lc1 = Utils.toLowerCase(ch1);
               char lc2 = Utils.toLowerCase(ch2);
               if (lc1 == lc2) {
                  cost = 32;
               } else {
                  char ac1 = this.toAccentless(lc1);
                  char ac2 = this.toAccentless(lc2);
                  if (ac1 == ac2) {
                     cost = 64;
                  } else if (KeypadLayout.isAdjacent(ac1, ac2)) {
                     cost = 128;
                  } else if (KeypadLayout.isShared(ac1, ac2)) {
                     cost = 64;
                  } else {
                     cost = 256;
                  }
               }
            }

            int dist = this.getDistance(rowLen, j - 1, i) + 256;
            dist = Math.min(dist, this.getDistance(rowLen, j, i - 1) + 256);
            dist = Math.min(dist, this.getDistance(rowLen, j - 1, i - 1) + cost);
            if (j > 1 && i > 1 && w2[j - 2] == w1[i - 1] && w2[j - 1] == w1[i - 2]) {
               int transCost = this.getDistance(rowLen, j - 2, i - 2) + cost;
               if (transCost < dist) {
                  dist = transCost;
               }
            }

            dt[j * rowLen + i] = (short)dist;
         }
      }

      return this.getDistance(rowLen, len2, len1);
   }

   private void setDistance(int rowLen, int i, int j, short dist) {
      this._distTable[i * rowLen + j] = dist;
   }

   private short getDistance(int rowLen, int i, int j) {
      return this._distTable[i * rowLen + j];
   }

   private char toAccentless(char ch) {
      return AccentGrouping.toAccentless(ch, this._locale);
   }

   public byte getFirstDistance() {
      return this._firstDistance;
   }

   public byte getLastDistance() {
      return this._lastDistance;
   }

   public void setSearchLearningDB(boolean aVal) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void setSpecificData(int aPos, ExtendedCurrentVariant aVariant, boolean ignoreOrder) {
      int idx = ignoreOrder ? aPos : super._order[aPos];
      aVariant._distance = this._distance[idx];
   }

   @Override
   protected void removeSpecificData(int aId) {
      int from = aId + 1;
      System.arraycopy(this._distance, from, this._distance, aId, super._variantsCount - from);
      System.arraycopy(this._subDistance, from, this._subDistance, aId, super._variantsCount - from);
   }

   @Override
   public boolean isSpellCheck() {
      return true;
   }

   static {
      _conversionFunctionWeights[0] = 1;
      _conversionFunctionWeights[1] = 2;
      _conversionFunctionWeights[2] = 3;
      _conversionFunctionWeights[3] = 3;
      _conversionFunctionWeights[4] = 3;
      _conversionFunctionWeights[5] = 3;
      _conversionFunctionWeights[6] = 3;
      _conversionFunctionWeights[7] = 3;
      _conversionFunctionWeights[8] = 9;
      _conversionFunctionWeights[9] = 10;
      _conversionFunctionWeights[10] = 11;
      _conversionFunctionWeights[11] = 12;
      _conversionFunctionWeights[12] = 13;
      _conversionFunctionWeights[13] = 14;
   }
}
