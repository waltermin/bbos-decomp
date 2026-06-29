package net.rim.tid.im.conv.europe.repository;

import java.util.Vector;
import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.Utils;

public class WordLearningComplexPrefixTable extends LearningComplexPrefixTable {
   protected byte _type;

   public WordLearningComplexPrefixTable(int aLevel, LearningReader aReader, byte type) {
      super(aLevel, aReader);
      this._type = type;
   }

   @Override
   protected int addTable(byte[] aEncodedWord, int aEncodedLength, int aDummy, int aStart, boolean aIsExactMatch) {
      int fileSize = super._reader.getFileSize();
      int freq_len = 0;
      int word_len = aEncodedLength - super._prefixLength;
      if (!aIsExactMatch) {
         word_len--;
      }

      word_len--;
      if (this._type == 3) {
         word_len -= 2;
         freq_len = 2;
      }

      if (word_len == 0) {
         aEncodedWord[aEncodedLength++] = 0;
         word_len = 1;
      }

      int length;
      if (aIsExactMatch) {
         length = 1 + word_len + freq_len;
      } else {
         length = 4;
         length += 1 + word_len + freq_len;
      }

      if (aStart <= fileSize) {
         System.arraycopy(super._wordsDefTable, aStart, super._wordsDefTable, aStart + length, fileSize - aStart);
      }

      int offset = aStart;
      if (!aIsExactMatch) {
         super._wordsDefTable[offset++] = 0;
         super._wordsDefTable[offset++] = (byte)(length - 2);
         super._wordsDefTable[offset++] = 1;
         super._wordsDefTable[offset++] = aEncodedWord[0];
      }

      aEncodedWord[aEncodedLength - 1] = (byte)(aEncodedWord[aEncodedLength - 1] | 128);
      int start = aIsExactMatch ? super._prefixLength + 1 : super._prefixLength + 2;
      super._wordsDefTable[offset++] = aEncodedWord[0];
      if (this._type == 3) {
         System.arraycopy(aEncodedWord, 1, super._wordsDefTable, offset, 2);
         offset += 2;
         start += 2;
      }

      System.arraycopy(aEncodedWord, start, super._wordsDefTable, offset, word_len);
      return length;
   }

   @Override
   protected int initExactMatch(int aOffset) {
      return this._type == 3 ? aOffset + 4 : aOffset + 2;
   }

   @Override
   protected void addExactMatch(byte[] aEncoded, int aKeyLength, int aDummy, byte aPriority) {
      if (!super._hasExactMatch) {
         int grow = this.addTable(aEncoded, aKeyLength, aDummy, super._exactMatchStart, true);
         super._hasExactMatch = true;
         this.growFileSize(grow);
      } else {
         if (aPriority == 2) {
            int tempOffset = super._exactMatchStart;
            super._wordsDefTable[tempOffset++] = aEncoded[0];
            if (this._type == 3) {
               super._wordsDefTable[tempOffset++] = aEncoded[1];
               super._wordsDefTable[tempOffset++] = aEncoded[2];
            }
         }
      }
   }

   @Override
   public void readAndInsertExactMatch(ReIterator aPrefix, int aOffset, int aNextOffset, ResultContainer aRes, char[] aBuffer) {
      aOffset++;
      char freq = this._type == 3 ? (char)(super._wordsDefTable[aOffset++] << 8 | super._wordsDefTable[aOffset++] & 0xFF) : super._reader.getDefaultFrequency();
      int len = super._prefixLength;
      int coded = super._wordsDefTable[aOffset] & 127;
      if (coded != 0) {
         LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
         aBuffer[len++] = alphabet.charAt(coded);
      }

      ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
      insertedWord.setData(aBuffer, 0, len, freq);
      this.correctCase(insertedWord);
      insertedWord.setValidWord(insertedWord._length == super._prefixLength);
      aRes.insert(insertedWord, super._prefixLength, aPrefix, 1);
   }

   @Override
   public void trim(TrimController aTrimController, char[] buffer) {
      if (super._prefixLength > 0) {
         buffer[super._prefixLength - 1] = super._lastPrefixChar;
      }

      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      int end = super._alphLength + super._localAlphabetStart;
      int i = aTrimController.getStoredStartPos(super._prefixLength, super._localAlphabetStart);
      this.initTableIterator(i - super._localAlphabetStart);
      int oldSize = super._alphLength;
      boolean finished = false;

      while (i < end) {
         label47: {
            char ch1 = alphabet.charAt(super._wordsDefTable[i]);
            this.nextTableInfo(super._tableInfo);
            LearningPrefixTable table;
            if (super._tableInfo._isComplex) {
               table = super._reader.getComplexPrefixTable(super._prefixLength + 1);
            } else {
               int minTimeStamp = super._wordsDefTable[super._tableInfo._tableStart + 3] & 255;
               if (!aTrimController.isInOutdatedInterval(minTimeStamp)) {
                  break label47;
               }

               table = super._reader.getSimplePrefixTable(super._prefixLength + 1);
            }

            table.init(super._wordsDefTable, super._tableInfo._tableStart + 2, ch1, this, super._tableInfo._tableSize);
            table.trim(aTrimController, buffer);
            if (oldSize != super._alphLength) {
               i--;
               end--;
               oldSize = super._alphLength;
            }

            finished = aTrimController.isTrimmingFinished();
            if (finished) {
               break;
            }
         }

         i++;
      }

      if (finished || super._prefixLength == 0) {
         aTrimController.complexTableFinished(super._prefixLength, i - super._localAlphabetStart);
      }
   }

   private int fillWord(int aTableStart, int aTableEnd, char[] aBuffer) {
      int freq = 0;
      int offset = aTableStart + 1;
      if (this._type == 3) {
         freq = super._wordsDefTable[offset++] << 8 | super._wordsDefTable[offset++] & 255;
      }

      int coded = super._wordsDefTable[offset] & 127;
      if (coded != 0) {
         char[] buffer = new char[super._prefixLength];
         System.arraycopy(aBuffer, 0, buffer, 0, super._prefixLength);
         LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
         switch (alphabet.charAt(coded)) {
            case '\u0000':
               break;
            case '\u0001':
            default:
               buffer[0] = Utils.toUpperCase(aBuffer[0]);
               break;
            case '\u0002':
               for (int i = 0; i < super._prefixLength; i++) {
                  buffer[i] = Utils.toUpperCase(aBuffer[i]);
               }
               break;
            case '\u0003':
               buffer[1] = Utils.toUpperCase(aBuffer[1]);
               break;
            case '\u0004':
               buffer[0] = Utils.toUpperCase(aBuffer[0]);
               buffer[1] = Utils.toUpperCase(aBuffer[1]);
         }
      }

      return freq;
   }

   public void getExactMatch(int aTableStart, int aTableEnd, CustomWordsSyncManager manager, char[] aBuffer, String locale, byte type) {
      int freq = this.fillWord(aTableStart, aTableEnd, aBuffer);
      manager.addWord(aBuffer, 0, super._prefixLength, freq, locale, type);
   }

   @Override
   public void getExactMatch(int aTableStart, int aTableEnd, Vector aEntries, char[] aBuffer) {
      this.fillWord(aTableStart, aTableEnd, aBuffer);
      aEntries.addElement(new String(aBuffer, 0, super._prefixLength));
   }

   @Override
   public boolean removeWord(byte[] aEncodedWord, int aLength) {
      if (aLength == super._prefixLength && super._hasExactMatch) {
         int skipFreqSize = this._type == 3 ? 2 : 0;
         int encoded_len = skipFreqSize + 2;
         int fileSize = super._reader.getFileSize();
         System.arraycopy(
            super._wordsDefTable, super._exactMatchStart + encoded_len, super._wordsDefTable, super._exactMatchStart, fileSize - super._exactMatchStart
         );
         this.growFileSize(-encoded_len);
         super._wordsDefTable[super._localAlphabetStart - 1] = (byte)(super._wordsDefTable[super._localAlphabetStart - 1] & 127);
         return true;
      } else {
         return false;
      }
   }
}
