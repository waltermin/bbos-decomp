package net.rim.tid.im.conv.europe.repository;

import java.util.Vector;
import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.Utils;

public class WordLearningSimplePrefixTable extends LearningSimplePrefixTable {
   protected int _skipFreqSize;
   private int _trimStartOffset;
   private int _trimStartId;
   private int _trimmedSize;
   private int _currFileSize;
   private int _trimmedWordCount;
   private WordLearningSimplePrefixTable$ReadInfo _info = new WordLearningSimplePrefixTable$ReadInfo();
   public static final int MAX_WORDS_PER_TABLE = 255;
   static final int ACTION_ADD = 0;
   static final int ACTION_UPDATE_TIME_STAMP = 1;

   public WordLearningSimplePrefixTable(int aLevel, LearningReader aReader, byte type) {
      super(aLevel, aReader);
      super._type = type;
      this._skipFreqSize = super._type == 3 ? 2 : 0;
   }

   @Override
   public void getMatches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate, char[] buff, ResultContainer result) {
      WordLearningSimplePrefixTable$SimplePrefixTableWord tableWord = new WordLearningSimplePrefixTable$SimplePrefixTableWord(this);
      WordLearningSimplePrefixTable$SimplePrefixTableWordIterator iter = new WordLearningSimplePrefixTable$SimplePrefixTableWordIterator(this);
      iter.reset();
      long mark = state.mark();
      char ch0 = buff[0];
      boolean needRollback = false;

      label61:
      while (iter.hasNext()) {
         iter.next(tableWord);
         if (needRollback) {
            needRollback = false;
            state.rollback(mark);
         }

         int buffIndex = super._prefixLength;
         int tableWordLen = tableWord.length();
         int actualWordLen = tableWordLen + super._prefixLength;
         if (expr.acceptsLength(state, actualWordLen, true, false)) {
            LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

            for (int i = 0; i < tableWordLen; i++) {
               int coded = tableWord.codedCharAt(i);
               char ch = alphabet.charAt(coded);
               if (!expr.accept(state, ch)) {
                  continue label61;
               }

               needRollback = true;
               buff[buffIndex++] = ch;
            }

            if (state.isFinal()) {
               char freq = super._type == 3 ? tableWord.freq : super._reader.getDefaultFrequency();
               switch (tableWord.caseCorrection) {
                  case '\u0000':
                     buff[0] = ch0;
                     break;
                  case '\u0001':
                  default:
                     buff[0] = Utils.toUpperCase(buff[0]);
                     buff[1] = Utils.toLowerCase(buff[1]);
                     break;
                  case '\u0002':
                     for (int j = 0; j < buffIndex; j++) {
                        buff[j] = Utils.toUpperCase(buff[j]);
                     }
                     break;
                  case '\u0003':
                     buff[0] = ch0;
                     buff[1] = Utils.toUpperCase(buff[1]);
                     break;
                  case '\u0004':
                     buff[0] = Utils.toUpperCase(buff[0]);
                     buff[1] = Utils.toUpperCase(buff[1]);
               }

               ExtendedCurrentVariant insertedWord = result.getTempInsertedWordContainer();
               insertedWord.setData(buff, 0, buffIndex, freq);
               result.insertWord(insertedWord);
            }
         }
      }
   }

   @Override
   public boolean matches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate) {
      WordLearningSimplePrefixTable$SimplePrefixTableWord tableWord = new WordLearningSimplePrefixTable$SimplePrefixTableWord(this);
      WordLearningSimplePrefixTable$SimplePrefixTableWordIterator iter = new WordLearningSimplePrefixTable$SimplePrefixTableWordIterator(this);
      iter.reset();
      long mark = state.mark();

      label65:
      while (true) {
         int tableWordLen;
         LearningGlobalAlphabet alphabet;
         int start;
         label54:
         while (true) {
            if (!iter.hasNext()) {
               return false;
            }

            iter.next(tableWord);
            state.rollback(mark);
            tableWordLen = tableWord.length();
            int actualWordLen = tableWordLen + super._prefixLength;
            if (expr.acceptsLength(state, actualWordLen, true, false)) {
               alphabet = super._reader.getAlphabet();
               start = 0;
               if (super._prefixLength != 1 || tableWordLen <= 0) {
                  break;
               }

               switch (tableWord.caseCorrection) {
                  case '\u0002':
                     break label54;
                  case '\u0003':
                  case '\u0004':
                  default:
                     int coded = tableWord.codedCharAt(0);
                     char ch = alphabet.charAt(coded);
                     ch = Utils.toUpperCase(ch);
                     if (expr.accept(state, ch)) {
                        start = 1;
                        break label54;
                     }
               }
            }
         }

         for (int i = start; i < tableWordLen; i++) {
            int coded = tableWord.codedCharAt(i);
            char ch = alphabet.charAt(coded);
            if (!expr.accept(state, ch)) {
               continue label65;
            }
         }

         if (state.isFinal()) {
            if (super._type != 3) {
               super._reader.getDefaultFrequency();
            }

            if (cstate == null) {
               return true;
            }

            switch (tableWord.caseCorrection) {
               case '\u0000':
               case '\u0002':
                  return true;
               case '\u0001':
               default:
                  return cstate.checkCorrectCasePrefix(true, false);
               case '\u0003':
                  return cstate.checkCorrectCasePrefix(false, true);
               case '\u0004':
                  return cstate.checkCorrectCasePrefix(true, true);
            }
         }
      }
   }

   @Override
   public void getPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      aBuffer[super._prefixLength - 1] = super._lastPrefixChar;
      boolean isFastRegular = aRes.isFast() && !aRes.isPredictive();
      if (!aPrefix.hasWildcards()) {
         if (isFastRegular) {
            this.getPredictionsForFastRegular(aPrefix.getRe(), aPrefix.getReLength(), aPrefix, aRes, aBuffer, aPrefix.isCaseSensitive());
         } else {
            this.getPredictions(aPrefix.getRe(), aPrefix.getReLength(), aPrefix, aRes, aBuffer, aPrefix.isCaseSensitive());
         }
      } else if (isFastRegular) {
         this.getPredictionsForFastRegular(aPrefix, aRes, aBuffer);
      } else {
         int offset = super._wordsDataOffset + 1 + super._wordsCount;
         LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
         int aPrefixLen = aPrefix.getLength();

         for (int i = 0; i < super._wordsCount; i++) {
            int freqOffset = offset;
            offset += this._skipFreqSize;
            aPrefix.pushState();
            boolean accepted = true;
            boolean hasCaseControl = false;
            int bufIndex = super._prefixLength;

            byte coded;
            do {
               coded = super._wordsDefTable[offset];
               if (accepted) {
                  int cod = coded & 127;
                  if (cod != 0) {
                     char ch = alphabet.charAt(cod);
                     if (ch < 5) {
                        aBuffer[bufIndex++] = ch;
                        hasCaseControl = true;
                     } else if (bufIndex < aPrefixLen && !aPrefix.accept(ch)) {
                        accepted = false;
                     } else {
                        aBuffer[bufIndex++] = ch;
                        aPrefix.nextWildcard();
                     }
                  }
               }

               offset++;
            } while ((coded & 128) == 0);

            if (accepted && aPrefix.getCurrentWildcardSubstIndex() != Integer.MAX_VALUE && bufIndex >= aPrefixLen) {
               char freq = super._type == 3
                  ? (char)(super._wordsDefTable[freqOffset] << 8 | super._wordsDefTable[freqOffset + 1] & 0xFF)
                  : super._reader.getDefaultFrequency();
               ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
               insertedWord.setData(aBuffer, 0, bufIndex, freq);
               if (hasCaseControl) {
                  this.correctCase(insertedWord);
                  if (insertedWord._length >= aPrefixLen) {
                     insertedWord.setValidWord(insertedWord._length == aPrefixLen);
                     aRes.insert(insertedWord, aPrefix.getLength(), aPrefix, 1);
                  }
               } else {
                  insertedWord.setValidWord(insertedWord._length == aPrefixLen);
                  aRes.insert(insertedWord, aPrefix.getLength(), aPrefix, 1);
               }
            }

            aPrefix.popState();
         }
      }
   }

   private void getPredictions(char[] aPrefix, int aPrefixLen, ReIterator aPrefIterator, ResultContainer aRes, char[] aBuffer, boolean aCaseSensitive) {
      int offset = super._wordsDataOffset + 1 + super._wordsCount;
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

      for (int i = 0; i < super._wordsCount; i++) {
         int freqOffset = offset;
         offset += this._skipFreqSize;
         int index = super._prefixLength;
         boolean accepted = true;
         boolean hasCaseControl = false;
         int bufIndex = super._prefixLength;

         byte coded;
         do {
            coded = super._wordsDefTable[offset];
            if (accepted) {
               int cod = coded & 127;
               if (cod != 0) {
                  char ch = alphabet.charAt(cod);
                  if (ch < 5) {
                     aBuffer[bufIndex++] = ch;
                     hasCaseControl = true;
                  } else {
                     char cmp_char = ch;
                     if (!aCaseSensitive && bufIndex < aPrefixLen && aPrefix[index] != cmp_char) {
                        cmp_char = Utils.toLowerCase(ch);
                     }

                     if (bufIndex <= aRes.getMaxWordLength() && (bufIndex >= aPrefixLen || aPrefix[index] == cmp_char)) {
                        aBuffer[bufIndex++] = ch;
                        index++;
                     } else {
                        accepted = false;
                     }
                  }
               }
            }

            offset++;
         } while ((coded & 128) == 0);

         if (accepted && bufIndex >= aPrefixLen) {
            char freq = super._type == 3
               ? (char)(super._wordsDefTable[freqOffset] << 8 | super._wordsDefTable[freqOffset + 1] & 0xFF)
               : super._reader.getDefaultFrequency();
            ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
            insertedWord.setData(aBuffer, 0, bufIndex, freq);
            if (hasCaseControl) {
               this.correctCase(insertedWord);
               if (insertedWord._length >= aPrefixLen) {
                  insertedWord.setValidWord(insertedWord._length == aPrefixLen);
                  aRes.insert(insertedWord, aPrefixLen, aPrefIterator, 1);
               }
            } else {
               insertedWord.setValidWord(insertedWord._length == aPrefixLen);
               aRes.insert(insertedWord, aPrefixLen, aPrefIterator, 1);
            }
         }
      }
   }

   private void getPredictionsForFastRegular(
      char[] aPrefix, int aPrefixLen, ReIterator aPrefixIter, ResultContainer aRes, char[] aBuffer, boolean aCaseSensitive
   ) {
      int prefixLen = aPrefixLen;
      int offset = super._wordsDataOffset + 1 + super._wordsCount;
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();
      int bufIndex = 0;

      for (int i = 0; i < super._wordsCount; i++) {
         int freqOffset = offset;
         offset += this._skipFreqSize;
         boolean accepted = true;
         boolean hasCaseControl = false;
         int lastBufIndex = bufIndex;
         bufIndex = super._prefixLength;

         byte coded;
         do {
            coded = super._wordsDefTable[offset++];
            if (coded != -128 && accepted) {
               int cod = coded & 127;
               if (bufIndex >= prefixLen && cod >= 5) {
                  aBuffer[bufIndex++] = 'a';
                  accepted = false;
               } else {
                  char ch = alphabet.charAt(cod);
                  if (ch < 5) {
                     aBuffer[bufIndex++] = ch;
                     hasCaseControl = true;
                  } else {
                     char cmp_char = ch;
                     if (!aCaseSensitive && bufIndex < aPrefixLen && aPrefix[bufIndex] != cmp_char) {
                        cmp_char = Utils.toLowerCase(ch);
                     }

                     if (bufIndex <= aRes.getMaxWordLength() && (bufIndex >= aPrefixLen || aPrefix[bufIndex] == cmp_char)) {
                        aBuffer[bufIndex++] = ch;
                     } else {
                        accepted = false;
                     }
                  }
               }
            }
         } while ((coded & 128) == 0);

         if (bufIndex >= prefixLen) {
            char freq = super._type == 3
               ? (char)(super._wordsDefTable[freqOffset] << 8 | super._wordsDefTable[freqOffset + 1] & 0xFF)
               : super._reader.getDefaultFrequency();
            ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
            insertedWord.setData(aBuffer, 0, bufIndex, freq);
            boolean insert = true;
            if (hasCaseControl) {
               this.correctCase(insertedWord);
               insert = insertedWord._length >= prefixLen;
            }

            if (insert) {
               insertedWord.setValidWord(insertedWord._length == aPrefixLen);
               aRes.insert(insertedWord, prefixLen, aPrefixIter, 1);
               if (aRes.isFull()) {
                  return;
               }
            }
         } else {
            if (hasCaseControl) {
               bufIndex--;
            }

            if (bufIndex < lastBufIndex) {
               return;
            }
         }
      }
   }

   void getPredictionsForFastRegular(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      int prefixLen = aPrefix.getLength();
      int offset = super._wordsDataOffset + 1 + super._wordsCount;
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

      for (int i = 0; i < super._wordsCount; i++) {
         int freqOffset = offset;
         offset += this._skipFreqSize;
         aPrefix.pushState();
         boolean accepted = true;
         boolean hasCaseControl = false;
         int bufIndex = super._prefixLength;

         byte coded;
         do {
            coded = super._wordsDefTable[offset++];
            if (coded != -128 && accepted) {
               int cod = coded & 127;
               if (bufIndex >= prefixLen && cod >= 5) {
                  aBuffer[bufIndex++] = 'a';
                  accepted = false;
               } else {
                  char ch = alphabet.charAt(cod);
                  if (ch < 5) {
                     aBuffer[bufIndex++] = ch;
                     hasCaseControl = true;
                  } else if (bufIndex < prefixLen && !aPrefix.accept(ch)) {
                     accepted = false;
                  } else if (ch != 0) {
                     aBuffer[bufIndex++] = ch;
                     aPrefix.nextWildcard();
                  }
               }
            }
         } while ((coded & 128) == 0);

         if (bufIndex >= prefixLen) {
            char freq = super._type == 3
               ? (char)(super._wordsDefTable[freqOffset] << 8 | super._wordsDefTable[freqOffset + 1] & 0xFF)
               : super._reader.getDefaultFrequency();
            ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
            insertedWord.setData(aBuffer, 0, bufIndex, freq);
            boolean insert = true;
            if (hasCaseControl) {
               this.correctCase(insertedWord);
               insert = insertedWord._length >= prefixLen;
            }

            if (insert) {
               insertedWord.setValidWord(insertedWord._length == prefixLen);
               aRes.insert(insertedWord, prefixLen, aPrefix, 1);
            }
         }

         aPrefix.popState();
      }
   }

   private void getFirstWordsForFastRegular(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
      int offset = super._wordsDataOffset + 1 + super._wordsCount;
      int cod = super._wordsDefTable[offset] & 127;
      int bufIndex = super._prefixLength;
      int count;
      int freq;
      boolean isValidWord;
      if (cod < 5) {
         count = 1;
         freq = super._reader.getDefaultFrequency();
         isValidWord = true;
         if (cod != 0) {
            aBuffer[bufIndex++] = super._reader.getAlphabet().charAt(cod);
         }
      } else {
         count = Math.max(10, super._wordsCount);
         freq = super._reader.getDefaultFrequency() * count;
         isValidWord = false;
      }

      insertedWord.setData(aBuffer, 0, bufIndex, freq);
      insertedWord.setValidWord(isValidWord);
      aRes.insertFast(insertedWord, aPrefix, count);
   }

   @Override
   public void add(byte[] aEncodedWord, int aEncodedLength, int aAction, int aWordOffset, byte aPriority) {
      if (super._wordsCount >= 255) {
         System.err.println("WordLearningSimplePrefixTable: unable to add word. critical words count reached.");
      } else {
         int offset = super._wordsDataOffset + 1 + super._wordsCount;
         int wordOffset = offset;
         int timeStampOffset = -1;

         label84:
         for (int i = 0; i < super._wordsCount; i++) {
            int bufIndex = super._prefixLength;
            int freqOffset = offset;
            offset += this._skipFreqSize;
            bufIndex += aWordOffset;
            boolean skip = false;

            byte coded;
            do {
               coded = super._wordsDefTable[offset];
               if (!skip) {
                  int cod = coded & 127;
                  if (bufIndex < aEncodedLength) {
                     if (cod > aEncodedWord[bufIndex]) {
                        timeStampOffset = super._wordsDataOffset + 1 + i;
                        break label84;
                     }

                     if (cod < aEncodedWord[bufIndex]) {
                        skip = true;
                        bufIndex++;
                     } else {
                        bufIndex++;
                     }
                  } else if (cod != 0) {
                     timeStampOffset = super._wordsDataOffset + 1 + i;
                     break label84;
                  }
               }

               offset++;
            } while ((coded & 128) == 0);

            if (!skip && bufIndex == aEncodedLength) {
               if (aPriority == 2) {
                  timeStampOffset = super._wordsDataOffset + 1 + i;
                  super._wordsDefTable[timeStampOffset] = aEncodedWord[0];
                  if (super._type == 3 && aAction != 1) {
                     super._wordsDefTable[freqOffset] = aEncodedWord[1];
                     super._wordsDefTable[freqOffset + 1] = aEncodedWord[2];
                  }
               }

               return;
            }

            wordOffset = offset;
         }

         int wordLen = aEncodedLength - super._prefixLength;
         int encodedLength = --wordLen;
         wordLen -= this._skipFreqSize;
         if (wordLen == 0) {
            aEncodedWord[aEncodedLength++] = 0;
            wordLen = 1;
            encodedLength++;
         }

         int oldFileSize = super._reader.getFileSize();
         System.arraycopy(super._wordsDefTable, wordOffset, super._wordsDefTable, wordOffset + encodedLength, oldFileSize - wordOffset);
         int encodedStart = super._prefixLength + 1;
         if (super._type == 3) {
            System.arraycopy(aEncodedWord, 1, super._wordsDefTable, wordOffset, 2);
            encodedStart += 2;
            wordOffset += 2;
         }

         aEncodedWord[aEncodedLength - 1] = (byte)(aEncodedWord[aEncodedLength - 1] | 128);
         System.arraycopy(aEncodedWord, encodedStart, super._wordsDefTable, wordOffset, wordLen);
         if (timeStampOffset == -1) {
            timeStampOffset = super._wordsDataOffset + 1 + super._wordsCount;
         }

         System.arraycopy(super._wordsDefTable, timeStampOffset, super._wordsDefTable, timeStampOffset + 1, oldFileSize + encodedLength - timeStampOffset);
         super._wordsDefTable[timeStampOffset] = aEncodedWord[0];
         super._wordsCount++;
         super._wordsDefTable[super._wordsDataOffset - 1] = (byte)super._wordsCount;
         this.growFileSize(encodedLength + 1);
         if (super._wordsCount == 1) {
            super._wordsDefTable[super._wordsDataOffset] = aEncodedWord[0];
         } else if (aPriority == 1) {
            super._wordsDefTable[super._wordsDataOffset] = super._reader.minTimeStamp(super._wordsDefTable[super._wordsDataOffset], aEncodedWord[0]);
         }

         if (super._wordsCount > this.getSplitSize()
            && super._reader.getMaxSplitNestingLevel() > super._prefixLength
            && super._reader.getFileSize() + 100 < super._reader.getSizeLimit()) {
            this.splitTable();
         } else {
            if (super._wordsCount == 255) {
               System.err.println("WordLearningSimplePrefixTable:critical count reached while adding new word");
            }
         }
      }
   }

   private void splitTable() {
      System.out.println("Splitting the table...");
      int globalAlphaLen = super._reader.getAlphabet().length();
      byte[] localAlphabet = new byte[globalAlphaLen];
      int alphaLen = 0;
      int complexTableSize = 0;
      int[] simpleTableSize = new int[globalAlphaLen];
      int[] simpleTableOffset = new int[globalAlphaLen];
      int[] simpleTableWordNo = new int[globalAlphaLen];
      byte[] simpleTableMinTimeStamp = new byte[globalAlphaLen];
      int[] simpleTableTimeStampOffset = new int[globalAlphaLen];
      int exactMatchSize = 0;
      byte[] exactMatch = new byte[4];
      boolean hasExactMatch = false;
      int offset = super._wordsDataOffset + 1 + super._wordsCount;
      int copy_offset = 0;
      int last_char = 0;
      int minTimeStamp = Integer.MAX_VALUE;
      int timeStampOffset = super._wordsDataOffset + 1;
      TrimController controller = ((WordLearningReader)super._reader).getTrimController();

      for (int i = 0; i < super._wordsCount; timeStampOffset++) {
         int freqOffset = offset;
         int timeStamp = super._wordsDefTable[timeStampOffset] & 255;
         offset += this._skipFreqSize;
         boolean first_char = true;

         byte coded;
         do {
            coded = super._wordsDefTable[offset];
            if (!first_char) {
               super._wordsDefTable[copy_offset++] = coded;
            } else {
               int cod = coded & 127;
               if (cod < 5) {
                  hasExactMatch = true;
                  exactMatchSize = 0;
                  exactMatch[exactMatchSize++] = super._wordsDefTable[timeStampOffset];
                  if (super._type == 3) {
                     exactMatch[exactMatchSize++] = super._wordsDefTable[freqOffset];
                     exactMatch[exactMatchSize++] = super._wordsDefTable[freqOffset + 1];
                  }

                  exactMatch[exactMatchSize++] = coded;
               } else {
                  if (cod != last_char) {
                     if (last_char != 0) {
                        int size = copy_offset - simpleTableOffset[alphaLen - 1];
                        simpleTableSize[alphaLen - 1] = size;
                        complexTableSize += size;
                        complexTableSize += simpleTableWordNo[alphaLen - 1];
                     }

                     localAlphabet[alphaLen] = (byte)cod;
                     simpleTableOffset[alphaLen] = freqOffset;
                     simpleTableTimeStampOffset[alphaLen] = timeStampOffset;
                     if (alphaLen > 0) {
                        simpleTableMinTimeStamp[alphaLen - 1] = (byte)minTimeStamp;
                     }

                     alphaLen++;
                     minTimeStamp = timeStamp;
                     last_char = cod;
                     copy_offset = offset;
                     if (cod != coded) {
                        super._wordsDefTable[copy_offset++] = -128;
                     }
                  } else {
                     minTimeStamp = controller.min(minTimeStamp, timeStamp);
                     if (super._type == 3) {
                        super._wordsDefTable[copy_offset++] = super._wordsDefTable[freqOffset];
                        super._wordsDefTable[copy_offset++] = super._wordsDefTable[freqOffset + 1];
                     }
                  }

                  simpleTableWordNo[alphaLen - 1]++;
               }

               first_char = false;
            }

            offset++;
         } while ((coded & 128) == 0);

         i++;
      }

      int size = copy_offset - simpleTableOffset[alphaLen - 1];
      simpleTableSize[alphaLen - 1] = size;
      simpleTableMinTimeStamp[alphaLen - 1] = (byte)minTimeStamp;
      complexTableSize += size;
      complexTableSize += simpleTableWordNo[alphaLen - 1];
      complexTableSize += 1 + alphaLen;
      complexTableSize += exactMatchSize;
      complexTableSize += alphaLen * 4;
      byte[] newTable = new byte[complexTableSize + 2];
      offset = 0;
      newTable[offset++] = (byte)(complexTableSize >> 8 | 128);
      newTable[offset++] = (byte)complexTableSize;
      if (hasExactMatch) {
         newTable[offset++] = (byte)(alphaLen | 128);
      } else {
         newTable[offset++] = (byte)alphaLen;
      }

      System.arraycopy(localAlphabet, 0, newTable, offset, alphaLen);
      offset += alphaLen;
      System.arraycopy(exactMatch, 0, newTable, offset, exactMatchSize);
      offset += exactMatchSize;

      for (int i = 0; i < alphaLen; i++) {
         size = simpleTableSize[i] + 1 + 1 + simpleTableWordNo[i];
         newTable[offset++] = (byte)(size >> 8);
         newTable[offset++] = (byte)size;
         newTable[offset++] = (byte)simpleTableWordNo[i];
         newTable[offset++] = simpleTableMinTimeStamp[i];
         System.arraycopy(super._wordsDefTable, simpleTableTimeStampOffset[i], newTable, offset, simpleTableWordNo[i]);
         offset += simpleTableWordNo[i];
         System.arraycopy(super._wordsDefTable, simpleTableOffset[i], newTable, offset, simpleTableSize[i]);
         offset += simpleTableSize[i];
      }

      if (offset != complexTableSize + 2) {
         throw new Object("");
      }

      int startOffset = super._wordsDataOffset - 3;
      int oldEndOffset = super._wordsDataOffset - 1 + super._size;
      int newEndOffset = startOffset + 2 + complexTableSize;
      System.arraycopy(super._wordsDefTable, oldEndOffset, super._wordsDefTable, newEndOffset, super._reader.getFileSize() - oldEndOffset);
      System.arraycopy(newTable, 0, super._wordsDefTable, startOffset, 2 + complexTableSize);
      super._size = complexTableSize;
      super._parent.growFileSize(newEndOffset - oldEndOffset);
      super._reader.updateNestingLevel(super._prefixLength + 2);
      super._reader.splitOccurred(alphaLen);
   }

   @Override
   public boolean removeWord(byte[] aEncodedWord, int aLength) {
      int offset = super._wordsDataOffset + 1 + super._wordsCount;

      for (int i = 0; i < super._wordsCount; i++) {
         int bufIndex = super._prefixLength;
         int length = aLength;
         int freqOffset = offset;
         offset += this._skipFreqSize;
         boolean accepted = true;

         byte coded;
         do {
            coded = super._wordsDefTable[offset];
            if (accepted) {
               int cod = coded & 127;
               if ((bufIndex >= length || cod != aEncodedWord[bufIndex]) && cod != 0) {
                  accepted = false;
               } else {
                  if (cod == 0) {
                     length++;
                  }

                  bufIndex++;
               }
            }

            offset++;
         } while ((coded & 128) == 0);

         if (accepted && bufIndex == length) {
            int encoded_len = length - super._prefixLength;
            encoded_len += this._skipFreqSize;
            int fileSize = super._reader.getFileSize();
            System.arraycopy(super._wordsDefTable, freqOffset + encoded_len, super._wordsDefTable, freqOffset, fileSize - freqOffset - encoded_len);
            int timeStampOffset = super._wordsDataOffset + 1 + i;
            System.arraycopy(super._wordsDefTable, timeStampOffset + 1, super._wordsDefTable, timeStampOffset, fileSize - encoded_len - timeStampOffset);
            super._wordsCount--;
            super._wordsDefTable[super._wordsDataOffset - 1] = (byte)super._wordsCount;
            this.growFileSize(-encoded_len - 1);
            return true;
         }
      }

      return false;
   }

   @Override
   public void trim(TrimController aTrimController, char[] buffer) {
      this.initTrim();
      int offset = super._wordsDataOffset + 1;
      int newLeastTimeStamp = aTrimController.getOutdatedIntervalLength();
      int newMinTimeStamp = Integer.MAX_VALUE;
      int count = super._wordsCount;
      if (aTrimController.isCurrentTimeGreater()) {
         for (int i = 0; i < count; offset++) {
            int timeStamp = super._wordsDefTable[offset] & 255;
            if (timeStamp < newLeastTimeStamp) {
               this.trimWord(i, offset--, aTrimController, buffer);
            } else {
               newMinTimeStamp = Math.min(newMinTimeStamp, timeStamp);
            }

            i++;
         }
      } else if (aTrimController.isOutdatedIntervalSplit()) {
         int currTimeStamp = aTrimController.getCurrentTimeStamp();

         for (int i = 0; i < count; offset++) {
            int timeStamp = super._wordsDefTable[offset] & 255;
            if (timeStamp >= newLeastTimeStamp && timeStamp <= currTimeStamp) {
               newMinTimeStamp = aTrimController.min(newMinTimeStamp, timeStamp);
            } else {
               this.trimWord(i, offset--, aTrimController, buffer);
            }

            i++;
         }
      } else {
         int currTimeStamp = aTrimController.getCurrentTimeStamp();

         for (int i = 0; i < count; offset++) {
            int timeStamp = super._wordsDefTable[offset] & 255;
            if (timeStamp < newLeastTimeStamp && timeStamp > currTimeStamp) {
               this.trimWord(i, offset--, aTrimController, buffer);
            } else {
               newMinTimeStamp = aTrimController.min(newMinTimeStamp, timeStamp);
            }

            i++;
         }
      }

      if (aTrimController.getAction() == 2) {
         super._wordsDefTable[super._wordsDataOffset] = super._reader.minTimeStamp((byte)newMinTimeStamp, (byte)aTrimController.getNewLeastTimeStamp());
      } else {
         super._wordsDefTable[super._wordsDataOffset] = (byte)newMinTimeStamp;
      }

      this.growFileSize(-this._trimmedSize);
      aTrimController.trim(this._trimmedSize);
      super._wordsCount = super._wordsCount - this._trimmedWordCount;
      super._wordsDefTable[super._wordsDataOffset - 1] = (byte)super._wordsCount;
      if (super._wordsCount == 0) {
         ((LearningComplexPrefixTable)super._parent).removeSubTable(super._lastPrefixChar);
      }
   }

   private void initTrim() {
      this._trimStartOffset = super._wordsDataOffset + 1 + super._wordsCount;
      this._trimStartId = 0;
      this._trimmedSize = 0;
      this._currFileSize = super._reader.getFileSize();
      this._trimmedWordCount = 0;
   }

   private void trimWord(int aWordId, int aTimeStampOffset, TrimController aTrimController, char[] buffer) {
      if (aTrimController.getAction() != 1) {
         super._wordsDefTable[aTimeStampOffset] = (byte)aTrimController.getNewLeastTimeStamp();
      } else {
         buffer[super._prefixLength - 1] = super._lastPrefixChar;
         System.arraycopy(super._wordsDefTable, aTimeStampOffset + 1, super._wordsDefTable, aTimeStampOffset, this._currFileSize - aTimeStampOffset - 1);
         this._currFileSize--;
         this._trimmedSize++;
         this._trimStartOffset--;

         for (; this._trimStartId != aWordId; this._trimStartId++) {
            this._trimStartOffset = this._trimStartOffset + this._skipFreqSize;

            byte coded;
            do {
               coded = super._wordsDefTable[this._trimStartOffset++];
            } while (coded > 0);
         }

         int endOffset = this._trimStartOffset + this._skipFreqSize;
         this._info.set(buffer, super._prefixLength, endOffset);
         this.readWord(this._info);
         aTrimController.trimWord(this._info._buffer, 0, this._info._index);
         endOffset = this._info._offset;
         this._trimStartId++;
         int removeLen = endOffset - this._trimStartOffset;
         System.arraycopy(
            super._wordsDefTable,
            this._trimStartOffset + removeLen,
            super._wordsDefTable,
            this._trimStartOffset,
            this._currFileSize - this._trimStartOffset - removeLen
         );
         this._currFileSize -= removeLen;
         this._trimmedSize += removeLen;
         this._trimmedWordCount++;
      }
   }

   @Override
   public void getEntries(CustomWordsSyncManager manager, char[] aBuffer, String locale) {
      aBuffer[super._prefixLength - 1] = super._lastPrefixChar;
      int offset = super._wordsDataOffset + 1 + super._wordsCount;

      for (int i = 0; i < super._wordsCount; i++) {
         int freq = super._type == 3 ? super._wordsDefTable[offset++] << 8 | super._wordsDefTable[offset++] & 0xFF : 0;
         this._info.set(aBuffer, super._prefixLength, offset);
         this.readWord(this._info);
         offset = this._info._offset;
         manager.addWord(this._info._buffer, 0, this._info._index, freq, locale, super._type);
      }
   }

   private void readWord(WordLearningSimplePrefixTable$ReadInfo info) {
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

      byte coded;
      do {
         coded = super._wordsDefTable[info._offset];
         int cod = coded & 127;
         if (cod != 0) {
            if (cod < 5) {
               char[] buffer = new char[info._index];
               System.arraycopy(info._buffer, 0, buffer, 0, info._index);
               info._buffer = buffer;
               switch (alphabet.charAt(cod)) {
                  case '\u0000':
                     break;
                  case '\u0001':
                  default:
                     buffer[0] = Utils.toUpperCase(buffer[0]);
                     break;
                  case '\u0002':
                     for (int j = 0; j < info._index; j++) {
                        buffer[j] = Utils.toUpperCase(buffer[j]);
                     }
                     break;
                  case '\u0003':
                     buffer[1] = Utils.toUpperCase(buffer[1]);
                     break;
                  case '\u0004':
                     buffer[0] = Utils.toUpperCase(buffer[0]);
                     buffer[1] = Utils.toUpperCase(buffer[1]);
               }
            } else {
               info._buffer[info._index++] = alphabet.charAt(cod);
            }
         }

         info._offset++;
      } while ((coded & 128) == 0);
   }

   @Override
   public void getEntries(Vector aEntries, char[] aBuffer) {
      aBuffer[super._prefixLength - 1] = super._lastPrefixChar;
      int offset = super._wordsDataOffset + 1 + super._wordsCount;

      for (int i = 0; i < super._wordsCount; i++) {
         if (super._type == 3) {
            offset += 2;
         }

         this._info.set(aBuffer, super._prefixLength, offset);
         this.readWord(this._info);
         offset = this._info._offset;
         aEntries.addElement(new Object(this._info._buffer, 0, this._info._index));
      }
   }
}
