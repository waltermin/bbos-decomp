package net.rim.tid.im.conv.europe.repository;

import java.util.Vector;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.Utils;

public class LearningComplexPrefixTable extends LearningPrefixTable {
   protected int _alphLength;
   protected int _localAlphabetStart;
   protected int _subTablesStart;
   protected int _exactMatchStart;
   protected boolean _hasExactMatch;
   protected LearningComplexPrefixTable$TableInfo _tableInfo = new LearningComplexPrefixTable$TableInfo();
   private int _iteratorOffset;
   private static final int COUNT;

   public LearningComplexPrefixTable(int aLevel, LearningReader aReader) {
      super(aLevel, aReader);
   }

   @Override
   public void init(byte[] aWordsDefTable, int aOffset, char aLastPrefixChar, LearningPrefixTable aParent, int aSize) {
      super._lastPrefixChar = aLastPrefixChar;
      super._wordsDefTable = aWordsDefTable;
      super._parent = aParent;
      super._size = aSize;
      if (super._wordsDefTable[aOffset] < 0) {
         this._alphLength = super._wordsDefTable[aOffset++] & 127;
         this._hasExactMatch = true;
      } else {
         this._alphLength = super._wordsDefTable[aOffset++] & 255;
         this._hasExactMatch = false;
      }

      this._localAlphabetStart = aOffset;
      aOffset += this._alphLength;
      this._exactMatchStart = aOffset;
      if (this._hasExactMatch) {
         aOffset = this.initExactMatch(aOffset);
      }

      this._subTablesStart = aOffset;
   }

   protected int initExactMatch(int _1) {
      throw null;
   }

   @Override
   public void getMatches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate, char[] buff, ResultContainer result) {
      if (this._hasExactMatch && state.isFinal()) {
         ExtendedCurrentVariant insertedWord = result.getTempInsertedWordContainer();
         insertedWord.setData(buff, 0, super._prefixLength, 0);
         result.insertWord(insertedWord);
      }

      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      int ucmark = cstate.ucMark();
      RegularExpression$SimpleCharacterIterator acceptable = expr.getAcceptableChars(state, alphabet);

      while (acceptable.hasNext()) {
         long mark = state.mark();
         char ch = acceptable.next();
         if (expr.accept(state, ch)) {
            char lc;
            if (CharacterUtilities.isUpperCase(ch)) {
               cstate.setUpperCaseUsed(true, ch);
               lc = Utils.toLowerCase(ch);
            } else {
               lc = ch;
               cstate.setUpperCaseUsed(false, ch);
            }

            int code = alphabet.indexOf(lc);
            if (code != -1) {
               int position = this.getIndexInAlphabet(code);
               if (position != -1) {
                  this.getTableInfo(position, this._tableInfo);
                  LearningPrefixTable table;
                  if (this._tableInfo._isComplex) {
                     table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
                  } else {
                     table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
                  }

                  table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, lc, this, this._tableInfo._tableSize);
                  buff[super._prefixLength] = ch;
                  table.getMatches(expr, state, cstate, buff, result);
               }
            }

            cstate.ucRollback(ucmark);
            state.rollback(mark);
         }
      }

      acceptable.close();
   }

   @Override
   public boolean matches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate) {
      if (this._hasExactMatch && state.isFinal()) {
         return true;
      }

      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      int ucmark = cstate.ucMark();
      RegularExpression$SimpleCharacterIterator acceptable = expr.getAcceptableChars(state, alphabet);

      while (acceptable.hasNext()) {
         long mark = state.mark();
         char ch = acceptable.next();
         if (expr.accept(state, ch)) {
            char lc;
            if (CharacterUtilities.isUpperCase(ch)) {
               cstate.setUpperCaseUsed(true, ch);
               lc = Utils.toLowerCase(ch);
            } else {
               lc = ch;
               cstate.setUpperCaseUsed(false, ch);
            }

            int code = alphabet.indexOf(lc);
            if (code != -1) {
               int position = this.getIndexInAlphabet(code);
               if (position != -1) {
                  this.getTableInfo(position, this._tableInfo);
                  LearningPrefixTable table;
                  if (this._tableInfo._isComplex) {
                     table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
                  } else {
                     table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
                  }

                  table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, lc, this, this._tableInfo._tableSize);
                  if (table.matches(expr, state, cstate)) {
                     acceptable.close();
                     return true;
                  }
               }
            }

            cstate.ucRollback(ucmark);
            state.rollback(mark);
         }
      }

      acceptable.close();
      return false;
   }

   @Override
   public void getPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      if (super._prefixLength > 0) {
         aBuffer[super._prefixLength - 1] = super._lastPrefixChar;
      }

      int len = aPrefix.getLength();
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      if (len == super._prefixLength) {
         if (this._hasExactMatch) {
            this.readAndInsertExactMatch(aPrefix, this._exactMatchStart, this._subTablesStart, aRes, aBuffer);
            if (aRes.isFast() && !aRes.isPredictive()) {
               return;
            }
         } else if (aRes.isFast() && !aRes.isPredictive() && !super._reader.isFrequencyIncluded()) {
            this.insertExactMatchForFast(aPrefix, aRes, aBuffer);
            return;
         }
      }

      char ch = aPrefix.nextChar();
      if (ch == 4) {
         int end = this._alphLength + this._localAlphabetStart;

         for (int i = this._localAlphabetStart; i < end; i++) {
            char ch1 = alphabet.charAt(super._wordsDefTable[i]);
            this.getPredictionsRecursively(aPrefix, aRes, i - this._localAlphabetStart, ch1, aBuffer);
         }
      } else {
         for (; ch != 0; ch = aPrefix.nextChar()) {
            int code = alphabet.indexOf(ch);
            if (code != -1) {
               int position = this.getIndexInAlphabet(code);
               if (position != -1) {
                  this.getPredictionsRecursively(aPrefix, aRes, position, ch, aBuffer);
               }
            }
         }
      }
   }

   protected void readAndInsertExactMatch(ReIterator _1, int _2, int _3, ResultContainer _4, char[] _5) {
      throw null;
   }

   private void insertExactMatchForFast(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
      int freq = super._reader.getDefaultFrequency() * '\n';
      insertedWord.setData(aBuffer, 0, super._prefixLength, freq);
      insertedWord.setValidWord(false);
      aRes.insertFast(insertedWord, aPrefix, 10);
   }

   private void getPredictionsRecursively(ReIterator aPrefix, ResultContainer aRes, int aPosition, char aChar, char[] aBuffer) {
      aPrefix.pushState();
      this.getTableInfo(aPosition, this._tableInfo);
      LearningPrefixTable table;
      if (this._tableInfo._isComplex) {
         table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
      } else {
         table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
      }

      table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, aChar, this, this._tableInfo._tableSize);
      aPrefix.nextWildcard();
      table.getPredictions(aPrefix, aRes, aBuffer);
      aPrefix.popState();
   }

   public LearningPrefixTable findTableForEncodedWord(byte[] aEncodedWord, int aOffset, int aLen) {
      if (aLen == super._prefixLength) {
         return this;
      }

      int code = aEncodedWord[aOffset + super._prefixLength] & 255;
      char ch = super._reader.getAlphabet().charAt(code);
      int position = this.getIndexInAlphabet(code);
      if (position != -1) {
         this.getTableInfo(position, this._tableInfo);
         if (this._tableInfo._isComplex) {
            LearningComplexPrefixTable table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
            table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, ch, this, this._tableInfo._tableSize);
            return table.findTableForEncodedWord(aEncodedWord, aOffset, aLen);
         } else {
            LearningPrefixTable table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
            table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, ch, this, this._tableInfo._tableSize);
            return table;
         }
      } else {
         return this;
      }
   }

   public int getIndexInAlphabet(int code) {
      int end = this._alphLength + this._localAlphabetStart;

      for (int i = this._localAlphabetStart; i < end; i++) {
         if (super._wordsDefTable[i] == code) {
            return i - this._localAlphabetStart;
         }
      }

      return -1;
   }

   protected void getTableInfo(int aIndex, LearningComplexPrefixTable$TableInfo aInfo) {
      int offset = this._subTablesStart;

      for (int i = 0; i < aIndex; i++) {
         int next_offset = Utils.bytes2Int(super._wordsDefTable[offset] & 127, super._wordsDefTable[offset + 1] & 255);
         offset += next_offset + 2;
      }

      aInfo._isComplex = (super._wordsDefTable[offset] & 128) != 0;
      aInfo._tableStart = offset;
      aInfo._tableSize = Utils.bytes2Int(super._wordsDefTable[offset] & 127, super._wordsDefTable[offset + 1] & 255);
   }

   protected void initTableIterator() {
      this._iteratorOffset = this._subTablesStart;
   }

   protected void initTableIterator(int aSkipCount) {
      this._iteratorOffset = this._subTablesStart;

      while (aSkipCount > 0) {
         int tableSize = Utils.bytes2Int(super._wordsDefTable[this._iteratorOffset] & 127, super._wordsDefTable[this._iteratorOffset + 1] & 255);
         this._iteratorOffset += tableSize + 2;
         aSkipCount--;
      }
   }

   protected void nextTableInfo(LearningComplexPrefixTable$TableInfo aInfo) {
      aInfo._isComplex = (super._wordsDefTable[this._iteratorOffset] & 128) != 0;
      aInfo._tableStart = this._iteratorOffset;
      aInfo._tableSize = Utils.bytes2Int(super._wordsDefTable[this._iteratorOffset] & 127, super._wordsDefTable[this._iteratorOffset + 1] & 255);
      this._iteratorOffset = this._iteratorOffset + aInfo._tableSize + 2;
   }

   @Override
   public void add(byte[] aEncoded, int aKeyLength, int aReplacementLength, int aWordOffset, byte aPriority) {
      if (aKeyLength - aWordOffset == super._prefixLength) {
         this.addExactMatch(aEncoded, aKeyLength, aReplacementLength, aPriority);
         super._wordsDefTable[this._localAlphabetStart - 1] = (byte)(this._alphLength | 128);
      } else {
         int fileSize = super._reader.getFileSize();
         if (fileSize >= this._exactMatchStart) {
            System.arraycopy(super._wordsDefTable, this._exactMatchStart, super._wordsDefTable, this._exactMatchStart + 1, fileSize - this._exactMatchStart);
         }

         this._alphLength++;
         super._wordsDefTable[this._localAlphabetStart - 1] = (byte)(this._hasExactMatch ? this._alphLength | 128 : this._alphLength);
         super._wordsDefTable[this._exactMatchStart++] = aEncoded[super._prefixLength + aWordOffset];
         this._subTablesStart++;
         this.growFileSize(1);
         this.getTableInfo(this._alphLength - 1, this._tableInfo);
         int grow = this.addTable(aEncoded, aKeyLength, aReplacementLength, this._tableInfo._tableStart, false);
         this.growFileSize(grow);
      }
   }

   protected void addExactMatch(byte[] _1, int _2, int _3, byte _4) {
      throw null;
   }

   @Override
   public void growFileSize(int aGrow) {
      if (super._parent == null) {
         super._reader.growFileSize(aGrow);
      } else {
         super._size += aGrow;
         super._wordsDefTable[this._localAlphabetStart - 3] = (byte)(super._size >>> 8 | 128);
         super._wordsDefTable[this._localAlphabetStart - 2] = (byte)super._size;
         super._parent.growFileSize(aGrow);
      }

      this._iteratorOffset += aGrow;
   }

   protected int addTable(byte[] _1, int _2, int _3, int _4, boolean _5) {
      throw null;
   }

   @Override
   public int trim(int aMaxCount) {
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      int end = this._alphLength + this._localAlphabetStart;
      int total_trimmed_size = 0;

      for (int i = this._localAlphabetStart; i < end; i++) {
         char ch1 = alphabet.charAt(super._wordsDefTable[i]);
         this.getTableInfo(i - this._localAlphabetStart, this._tableInfo);
         LearningPrefixTable table;
         if (this._tableInfo._isComplex) {
            table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
         } else {
            table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
         }

         table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, ch1, this, this._tableInfo._tableSize);
         total_trimmed_size += table.trim(aMaxCount);
      }

      return total_trimmed_size;
   }

   @Override
   public int getEntryNo() {
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      int element_no = 0;
      if (this._hasExactMatch) {
         element_no++;
      }

      int end = this._alphLength + this._localAlphabetStart;

      for (int i = this._localAlphabetStart; i < end; i++) {
         char ch1 = alphabet.charAt(super._wordsDefTable[i]);
         this.getTableInfo(i - this._localAlphabetStart, this._tableInfo);
         LearningPrefixTable table;
         if (this._tableInfo._isComplex) {
            table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
         } else {
            table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
         }

         table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, ch1, this, this._tableInfo._tableSize);
         element_no += table.getEntryNo();
      }

      return element_no;
   }

   @Override
   public void getEntries(CustomWordsSyncManager manager, char[] aBuffer, String locale) {
      if (super._prefixLength > 0) {
         aBuffer[super._prefixLength - 1] = super._lastPrefixChar;
      }

      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      if (this._hasExactMatch) {
         this.getExactMatch(this._exactMatchStart, this._subTablesStart, manager, aBuffer, locale);
      }

      int end = this._alphLength + this._localAlphabetStart;

      for (int i = this._localAlphabetStart; i < end; i++) {
         char ch1 = alphabet.charAt(super._wordsDefTable[i]);
         this.getTableInfo(i - this._localAlphabetStart, this._tableInfo);
         LearningPrefixTable table;
         if (this._tableInfo._isComplex) {
            table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
         } else {
            table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
         }

         table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, ch1, this, this._tableInfo._tableSize);
         table.getEntries(manager, aBuffer, locale);
      }
   }

   @Override
   public void getEntries(Vector aEntries, char[] aBuffer) {
      if (super._prefixLength > 0) {
         aBuffer[super._prefixLength - 1] = super._lastPrefixChar;
      }

      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      if (this._hasExactMatch) {
         this.getExactMatch(this._exactMatchStart, this._subTablesStart, aEntries, aBuffer);
      }

      int end = this._alphLength + this._localAlphabetStart;

      for (int i = this._localAlphabetStart; i < end; i++) {
         char ch1 = alphabet.charAt(super._wordsDefTable[i]);
         this.getTableInfo(i - this._localAlphabetStart, this._tableInfo);
         LearningPrefixTable table;
         if (this._tableInfo._isComplex) {
            table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
         } else {
            table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
         }

         table.init(super._wordsDefTable, this._tableInfo._tableStart + 2, ch1, this, this._tableInfo._tableSize);
         table.getEntries(aEntries, aBuffer);
      }
   }

   public void getExactMatch(int aTableStart, int aTableEnd, Vector aEntries, char[] aBuffer) {
   }

   public void getExactMatch(int aTableStart, int aTableEnd, CustomWordsSyncManager manager, char[] aBuffer, String locale) {
   }

   void removeSubTable(char aChar) {
      int code = super._reader.getAlphabet().indexOf(aChar);
      int position = this.getIndexInAlphabet(code);
      if (position != -1) {
         int fileSize = super._reader.getFileSize();
         this.getTableInfo(position, this._tableInfo);
         int start = this._tableInfo._tableStart;
         int end = start + this._tableInfo._tableSize + 2;
         System.arraycopy(super._wordsDefTable, end, super._wordsDefTable, start, fileSize - end);
         int trimmedSize = end - start;
         fileSize -= trimmedSize;
         start = this._localAlphabetStart + position;
         System.arraycopy(super._wordsDefTable, start + 1, super._wordsDefTable, start, fileSize - start - 1);
         this.growFileSize(-(++trimmedSize));
         this._alphLength--;
         super._wordsDefTable[this._localAlphabetStart - 1] = (byte)(this._hasExactMatch ? this._alphLength | 128 : this._alphLength);
         this._exactMatchStart--;
         this._subTablesStart--;
      }
   }
}
