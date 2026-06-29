package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.Utils;

public class SimplePrefixTable extends PrefixTable {
   private int iOffset;
   private int iWordsCount;
   private int iFreqCount;
   private int MAX_WORDS_PER_TABLE = 100;
   private int iWordsDataOffset;
   private boolean iHasExactMatch;
   private int iMaxFreq;
   private int iNextFreq;
   private int iFreqStep;
   private boolean iFreqIteratorStarted;
   private ExtendedCurrentVariant _extTempVariant = new ExtendedCurrentVariant();

   public SimplePrefixTable(int aLevel, Reader aReader) {
      super(aLevel, aReader);
   }

   public void setLevel(int aLevel) {
      super.iPrefixLength = aLevel;
   }

   @Override
   public void init(byte[] aWordsDefTable, int aOffset, char aLastPrefixChar) {
      this.init(aWordsDefTable, aOffset, aLastPrefixChar, this.iWordsCount, this.MAX_WORDS_PER_TABLE);
   }

   public void init(byte[] aWordsDefTable, int aOffset, char aLastPrefixChar, int aFreqNom, int aFreqDenom) {
      super.iLastPrefixChar = aLastPrefixChar;
      super.iWordsDefTable = aWordsDefTable;
      this.iOffset = aOffset;
      if (super.iWordsDefTable[this.iOffset] < 0) {
         this.iWordsCount = super.iWordsDefTable[this.iOffset] & 127;
         this.iFreqCount = this.iWordsCount == 1 ? 0 : this.iWordsCount;
         this.iHasExactMatch = true;
      } else {
         this.iWordsCount = super.iWordsDefTable[this.iOffset] & 255;
         this.iFreqCount = this.iWordsCount - 1;
         this.iHasExactMatch = false;
      }

      this.iWordsDataOffset = this.iOffset + 1;
      if (super.iReader.iIncludeFrequency) {
         this.iMaxFreq = Utils.bytes2Int(aWordsDefTable[this.iOffset + 1], aWordsDefTable[this.iOffset + 2]);
         this.iWordsDataOffset = this.iWordsDataOffset + this.iFreqCount + 2;
      } else if (aFreqDenom != this.MAX_WORDS_PER_TABLE) {
         this.iMaxFreq = 65535 * aFreqNom / aFreqDenom;
         this.iFreqStep = 65535 / aFreqDenom;
      } else {
         aFreqNom = this.iWordsCount;
         this.iMaxFreq = 65535 * aFreqNom / aFreqDenom;
         this.iFreqStep = this.iMaxFreq / this.iWordsCount;
      }
   }

   private int findWordsToRead(int aPrefLength, ResultContainer aRes, ReIterator aPrefix) {
      if (aRes.isSpellCheck()) {
         return this.iWordsCount;
      }

      int words_count = Integer.MAX_VALUE;
      if (aPrefLength == super.iPrefixLength) {
         words_count = aRes.getCapacity();

         for (int i = 0; i < aRes.getVariantsCount() && words_count > 0; i++) {
            aRes.getVariantAt(i, this._extTempVariant);
            if (aRes.getOriginalFrequency(this._extTempVariant) < this.iMaxFreq) {
               break;
            }

            words_count--;
         }

         if (this.iHasExactMatch) {
            words_count = this.iWordsCount;
         }
      }

      return words_count == 0
            && aRes.isFast()
            && aRes.getWildcardSubstCount() != null
            && aRes.getWildcardSubstCount()[aPrefix.getCurrentWildcardSubstIndex()] == 0
         ? 1
         : Math.min(words_count, this.iWordsCount);
   }

   public void getPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      if (aRes.isFast()) {
         if (!aRes.isPredictive()) {
            this.getPredictionsForFastRegular(aPrefix, aRes, aBuffer);
            return;
         }
      } else if (!aPrefix.hasWildcards()) {
         this.getPredictionsNoWildcards(aPrefix, aPrefix.getRe(), aPrefix.getReLength(), aRes, aBuffer, aPrefix.isCaseSensitive());
         return;
      }

      int prefix_len = aPrefix.hasQuantifiers() ? Integer.MAX_VALUE : aPrefix.getLength();
      int buf_index = super.iPrefixLength;
      aBuffer[buf_index - 1] = super.iLastPrefixChar;
      if (super.iPrefixLength == 2) {
         aBuffer[0] = aPrefix.getRe()[0];
      }

      int words_count = this.findWordsToRead(prefix_len, aRes, aPrefix);
      if (words_count != 0) {
         this.initFreqIterator();
         int offset = this.iWordsDataOffset;
         boolean check_prefixes_only = false;

         for (int i = 0; i < words_count; i++) {
            aPrefix.pushState();
            byte coded = 0;
            boolean accepted = true;
            boolean skip = false;

            do {
               coded = super.iWordsDefTable[offset++];
               if (coded != -128 && !skip) {
                  int cod = coded & 127;
                  if (check_prefixes_only && buf_index >= prefix_len && cod >= super.iReader._caseControlCount) {
                     aBuffer[buf_index++] = 'a';
                     skip = true;
                  } else if (cod < super.iReader.iSubstShift) {
                     char ch = super.iReader.iAlphabet.charAt(cod);
                     if (buf_index <= aRes.getMaxWordLength() && (buf_index >= prefix_len || aPrefix.accept(ch))) {
                        aBuffer[buf_index++] = ch;
                        aPrefix.nextWildcard();
                     } else {
                        accepted = false;
                        skip = true;
                     }
                  } else {
                     String subst = super.iReader.iSubstTable.getSubstitution(cod, super.iReader.iSubstShift);
                     int len = subst.length();
                     accepted = len + buf_index - 1 <= aRes.getMaxWordLength() && aPrefix.accept(subst);
                     if (accepted) {
                        for (int j = 0; j < len; j++) {
                           aBuffer[buf_index++] = subst.charAt(j);
                        }
                     } else {
                        skip = true;
                     }
                  }
               }
            } while ((coded & 128) == 0);

            boolean isCapitalPair = coded == super.iReader.CAPITAL_PAIR_BYTE;
            if (accepted && buf_index >= aPrefix.getLength()) {
               boolean inserted = this.insertWord(aBuffer, buf_index, prefix_len, aPrefix, isCapitalPair, aRes);
               if (!inserted && (i != 0 || !this.iHasExactMatch)) {
                  if (!aRes.isFast()) {
                     aPrefix.popState();
                     return;
                  }

                  check_prefixes_only = true;
               }
            } else {
               this.skipFreq(isCapitalPair);
            }

            if (isCapitalPair) {
               i++;
            }

            buf_index = super.iPrefixLength;
            aPrefix.popState();
         }
      }
   }

   private int firstFreqIndex() {
      return super.iReader.iIncludeFrequency ? this.iOffset + 3 : this.iMaxFreq;
   }

   private int nextFreqIndex(int currIndex) {
      return super.iReader.iIncludeFrequency ? currIndex + 1 : currIndex - this.iFreqStep;
   }

   private char freqAt(int index) {
      if (super.iReader.iIncludeFrequency) {
         int next = super.iWordsDefTable[index] & 255;
         return (char)(this.iMaxFreq * next / 255);
      } else {
         return (char)index;
      }
   }

   private void initFreqIterator() {
      if (super.iReader.iIncludeFrequency) {
         this.iFreqIteratorStarted = true;
      }

      this.iNextFreq = this.firstFreqIndex();
   }

   private char nextFreq() {
      if (!super.iReader.iIncludeFrequency) {
         char ret = this.freqAt(this.iNextFreq);
         this.iNextFreq = this.nextFreqIndex(this.iNextFreq);
         return ret;
      }

      char ret;
      if (!this.iFreqIteratorStarted || this.iHasExactMatch && this.iWordsCount != 1) {
         ret = this.freqAt(this.iNextFreq);
         this.iNextFreq = this.nextFreqIndex(this.iNextFreq);
      } else {
         ret = (char)this.iMaxFreq;
      }

      this.iFreqIteratorStarted = false;
      return ret;
   }

   private void skipFreq(boolean aIsCapitalPair) {
      if (!super.iReader.iIncludeFrequency) {
         this.iNextFreq = this.nextFreqIndex(this.iNextFreq);
      } else {
         if (!this.iFreqIteratorStarted || this.iHasExactMatch && this.iWordsCount > 1) {
            this.iNextFreq = this.nextFreqIndex(this.iNextFreq);
         }

         if (aIsCapitalPair) {
            this.iNextFreq = this.nextFreqIndex(this.iNextFreq);
         }

         this.iFreqIteratorStarted = false;
      }
   }

   public void getFirstWords(ReIterator aPrefix, int aPrefLength, int aCount, boolean aIncludeExactMatch, ResultContainer aRes, char[] aBuffer) {
      boolean isSpellCheck = aRes.isSpellCheck();
      if (!aRes.isPredictive() && !isSpellCheck) {
         this.getFirstWordsForFastRegular(aCount, aPrefix, aIncludeExactMatch, aRes, aBuffer);
      } else {
         this.initFreqIterator();
         int offset = this.iWordsDataOffset;

         for (int i = 0; i < aCount; i++) {
            offset = this.readAndInsertWord(offset, this.nextFreq(), aRes, aPrefLength, super.iPrefixLength, aBuffer, aPrefix, isSpellCheck);
         }
      }
   }

   public void getFirstWordsForFastRegular(int aCount, ReIterator aPrefix, boolean aIncludeExactMatch, ResultContainer aRes, char[] aBuffer) {
      int buffer_index = super.iPrefixLength;
      if (this.iHasExactMatch && aIncludeExactMatch) {
         byte ch = (byte)(super.iWordsDefTable[this.iWordsDataOffset] & 127);
         if (ch > 0) {
            aBuffer[buffer_index++] = (char)ch;
         }
      }

      aCount = Math.min(aCount, this.iWordsCount);
      int offset = this.iOffset + 3;
      int freq = this.iMaxFreq;
      int count = aCount - 1;
      if (this.iHasExactMatch && this.iWordsCount > 1 && aIncludeExactMatch) {
         aCount = 1;
         count = 1;
         freq = 0;
      }

      int freq_sum = 0;
      if (count > 0) {
         int first = super.iWordsDefTable[offset++] & 255;
         int last = 0;
         freq_sum += first;

         for (int i = 1; i < count; i++) {
            last = super.iWordsDefTable[offset++] & 255;
            freq_sum += last;
         }

         if (this.iHasExactMatch && !aIncludeExactMatch && last > first) {
            freq_sum -= first;
            count--;
         }
      }

      freq += freq_sum * this.iMaxFreq / 255;
      if (this.iHasExactMatch && aIncludeExactMatch && this.iWordsCount > 1) {
         aCount++;
      }

      ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
      insertedWord.setData(aBuffer, 0, buffer_index, freq);
      this.correctCase(insertedWord);
      insertedWord.setValidWord(this.iHasExactMatch && aIncludeExactMatch);
      aRes.insertFast(insertedWord, aPrefix, aCount);
      if (this.iHasExactMatch && aIncludeExactMatch) {
         offset = this.iWordsDataOffset + 1;
         this.initFreqIterator();
         this.skipFreq(false);

         for (int i = 1; i < this.iWordsCount; i++) {
            byte coded = 0;
            buffer_index = super.iPrefixLength;
            boolean accepted = true;

            do {
               coded = super.iWordsDefTable[offset++];
               if (accepted) {
                  int cod = coded & 127;
                  if (cod < super.iReader._caseControlCount) {
                     if (cod > 0) {
                        aBuffer[buffer_index++] = (char)cod;
                     }
                  } else {
                     accepted = false;
                  }
               }
            } while ((coded & 128) == 0);

            boolean isCapitalPair = coded == super.iReader.CAPITAL_PAIR_BYTE;
            if (accepted) {
               this.insertWord(aBuffer, buffer_index, -1, aPrefix, isCapitalPair, aRes);
            } else {
               this.skipFreq(isCapitalPair);
            }

            if (isCapitalPair) {
               i++;
            }
         }
      }
   }

   void getPredictionsForFastRegular(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      int prefix_len = aPrefix.getLength();
      int buf_index = super.iPrefixLength;
      aBuffer[buf_index - 1] = super.iLastPrefixChar;
      if (super.iPrefixLength == 2) {
         aBuffer[0] = aPrefix.getRe()[0];
      }

      if (prefix_len == super.iPrefixLength) {
         int FIRST_WORDS_NO = 10;
         this.getFirstWordsForFastRegular(10, aPrefix, true, aRes, aBuffer);
      } else {
         this.initFreqIterator();
         int offset = this.iWordsDataOffset;

         for (int i = 0; i < this.iWordsCount; i++) {
            aPrefix.pushState();
            byte coded = 0;
            boolean accepted = true;

            do {
               coded = super.iWordsDefTable[offset++];
               if (coded != -128 && accepted) {
                  int cod = coded & 127;
                  if (buf_index >= prefix_len && cod >= super.iReader._caseControlCount) {
                     aBuffer[buf_index++] = 'a';
                     accepted = false;
                  } else if (cod < super.iReader.iSubstShift) {
                     char ch = super.iReader.iAlphabet.charAt(cod);
                     if (buf_index < prefix_len && !aPrefix.accept(ch)) {
                        accepted = false;
                     } else if (ch != 0) {
                        aBuffer[buf_index++] = ch;
                        aPrefix.nextWildcard();
                     }
                  } else {
                     String subst = super.iReader.iSubstTable.getSubstitution(cod, super.iReader.iSubstShift);
                     accepted = aPrefix.accept(subst);
                     if (accepted) {
                        int len = subst.length();

                        for (int j = 0; j < len; j++) {
                           aBuffer[buf_index++] = subst.charAt(j);
                        }
                     }
                  }
               }
            } while ((coded & 128) == 0);

            boolean isCapitalPair = coded == super.iReader.CAPITAL_PAIR_BYTE;
            if (buf_index >= prefix_len) {
               this.insertWord(aBuffer, buf_index, prefix_len, aPrefix, isCapitalPair, aRes);
            } else {
               this.skipFreq(isCapitalPair);
            }

            if (isCapitalPair) {
               i++;
            }

            buf_index = super.iPrefixLength;
            aPrefix.popState();
         }
      }
   }

   private void getPredictionsNoWildcards(ReIterator aIter, char[] aPrefix, int aLen, ResultContainer aRes, char[] aBuffer, boolean aCaseSensitive) {
      int buf_index = super.iPrefixLength;
      aBuffer[buf_index - 1] = super.iLastPrefixChar;
      if (super.iPrefixLength == 2) {
         aBuffer[0] = aPrefix[0];
      }

      int words_count = this.iWordsCount;
      int offset = 0;
      int start_word_id = 0;
      this.initFreqIterator();
      offset = this.iWordsDataOffset;
      boolean check_prefixes_only = false;
      byte word_end_byte = -128;
      String alphabet = super.iReader.getAlphabet();
      int subst_shift = super.iReader.getSubstShift();
      Reader$SubstitutionTable subst_table = super.iReader.getSubstitutionTable();

      for (int i = start_word_id; i < words_count; i++) {
         int index = super.iPrefixLength;
         byte coded = 0;
         boolean accepted = true;
         boolean skip = false;

         do {
            coded = super.iWordsDefTable[offset++];
            if (coded != word_end_byte && !skip) {
               int cod = coded & 127;
               if (check_prefixes_only && buf_index >= aLen && cod >= super.iReader._caseControlCount) {
                  aBuffer[buf_index++] = 'a';
                  skip = true;
               } else if (cod < subst_shift) {
                  char ch = alphabet.charAt(cod);
                  char cmp_char = ch;
                  if (!aCaseSensitive && buf_index < aLen && aPrefix[index] != cmp_char) {
                     cmp_char = Utils.toLowerCase(ch);
                  }

                  if (buf_index <= aRes.getMaxWordLength() && (buf_index >= aLen || aPrefix[index] == cmp_char)) {
                     aBuffer[buf_index++] = ch;
                     index++;
                  } else {
                     accepted = false;
                     skip = true;
                  }
               } else {
                  String subst = subst_table.getSubstitution(cod, subst_shift);
                  int len = subst.length();
                  if (len + buf_index - 1 > aRes.getMaxWordLength()) {
                     accepted = false;
                     skip = true;
                  } else {
                     for (int j = 0; j < len; j++) {
                        char ch = subst.charAt(j);
                        char cmp_char = ch;
                        if (!aCaseSensitive) {
                           cmp_char = Utils.toLowerCase(ch);
                        }

                        if (buf_index < aLen && aPrefix[index++] != cmp_char) {
                           accepted = false;
                           skip = true;
                           break;
                        }

                        if (ch != 0) {
                           aBuffer[buf_index++] = ch;
                        }
                     }
                  }
               }
            }
         } while ((coded & 128) == 0);

         boolean isCapitalPair = coded == super.iReader.CAPITAL_PAIR_BYTE;
         if (accepted && buf_index >= aLen) {
            this.insertWord(aBuffer, buf_index, -1, aIter, isCapitalPair, aRes);
            if (aRes.isFull()) {
               return;
            }
         } else {
            this.skipFreq(isCapitalPair);
         }

         if (isCapitalPair) {
            i++;
         }

         buf_index = super.iPrefixLength;
      }
   }

   @Override
   public void getMatches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate, char[] buff, ResultContainer result) {
      if (!result.isFull()) {
         int substShift = super.iReader.getSubstShift();
         String alphabet = super.iReader.getAlphabet();
         Reader$SubstitutionTable substTable = super.iReader.getSubstitutionTable();
         Locale locale = Locale.getDefaultInputForSystem();
         SimplePrefixTable$SimplePrefixTableWord tableWord = new SimplePrefixTable$SimplePrefixTableWord(this);
         SimplePrefixTable$SimplePrefixTableWordIterator iter = new SimplePrefixTable$SimplePrefixTableWordIterator(this);
         iter.reset();
         Object mark = state.newMark();
         buff[super.iPrefixLength - 1] = super.iLastPrefixChar;
         boolean needRollback = false;
         char ch0 = buff[0];
         char ch1 = buff[1];

         label67:
         for (int freqIndex = this.firstFreqIndex(); iter.hasNext(); freqIndex = this.nextFreqIndex(freqIndex)) {
            iter.next(tableWord);
            if (needRollback) {
               needRollback = false;
               state.rollback(mark);
            }

            int buffIndex = super.iPrefixLength;
            int tableWordLen = tableWord.length();
            int actualWordLen = tableWordLen + super.iPrefixLength;
            if (expr.acceptsLength(state, actualWordLen, true, false)) {
               for (int i = 0; i < tableWordLen; i++) {
                  int coded = tableWord.codedCharAt(i);
                  if (coded < substShift) {
                     char ch = alphabet.charAt(coded);
                     if (!expr.accept(state, ch)) {
                        continue label67;
                     }

                     needRollback = true;
                     buff[buffIndex++] = ch;
                  } else {
                     String currentSubst = substTable.getSubstitution(coded, substShift);
                     int len = this.adjustWordForSubstitutionLengthAndCaseCorrection(currentSubst, tableWord);
                     actualWordLen += len - 1;
                     if (!expr.acceptsLength(state, actualWordLen, true, false)) {
                        continue label67;
                     }

                     needRollback = true;

                     for (int j = 0; j < len; j++) {
                        if (!expr.accept(state, currentSubst.charAt(j))) {
                           continue label67;
                        }

                        buff[buffIndex++] = currentSubst.charAt(j);
                     }
                  }
               }

               if (state.isFinal()) {
                  int caseCorrection = tableWord.caseCorrection;
                  this.adjustCase(caseCorrection, buff, buffIndex, ch0, ch1, locale);
                  ExtendedCurrentVariant insertedWord = result.getTempInsertedWordContainer();
                  insertedWord.setData(buff, 0, buffIndex, this.freqAt(freqIndex));
                  result.insertWord(insertedWord);
                  if (caseCorrection == 5) {
                     this.adjustCase(1, buff, buffIndex, ch0, ch1, locale);
                     freqIndex = this.nextFreqIndex(freqIndex);
                     insertedWord.setData(buff, 0, buffIndex, this.freqAt(freqIndex));
                     result.insertWord(insertedWord);
                  }

                  buff[super.iPrefixLength - 1] = super.iLastPrefixChar;
                  if (result.isFull()) {
                     break;
                  }
               }
            }
         }

         buff[0] = ch0;
         buff[1] = ch1;
      }
   }

   @Override
   public boolean matches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate) {
      int substShift = super.iReader.getSubstShift();
      String alphabet = super.iReader.getAlphabet();
      Reader$SubstitutionTable substTable = super.iReader.getSubstitutionTable();
      Locale locale = Locale.getDefaultInputForSystem();
      SimplePrefixTable$SimplePrefixTableWord tableWord = new SimplePrefixTable$SimplePrefixTableWord(this);
      SimplePrefixTable$SimplePrefixTableWordIterator iter = new SimplePrefixTable$SimplePrefixTableWordIterator(this);
      iter.reset();
      Object mark = state.newMark();

      label66:
      while (iter.hasNext()) {
         iter.next(tableWord);
         state.rollback(mark);
         int tableWordLen = tableWord.length();
         int actualWordLen = tableWordLen + super.iPrefixLength;
         if (expr.acceptsLength(state, actualWordLen, true, false)) {
            boolean uc = tableWord.caseCorrection == 2;

            for (int i = 0; i < tableWordLen; i++) {
               int coded = tableWord.codedCharAt(i);
               if (coded < substShift) {
                  char ch = alphabet.charAt(coded);
                  if (uc) {
                     ch = CaseCorrector.toUpperCase(ch, locale);
                  }

                  if (!expr.accept(state, ch)) {
                     continue label66;
                  }
               } else {
                  String currentSubst = substTable.getSubstitution(coded, substShift);
                  int len = this.adjustWordForSubstitutionLengthAndCaseCorrection(currentSubst, tableWord);
                  actualWordLen += len - 1;
                  if (!expr.acceptsLength(state, actualWordLen, true, false)) {
                     continue label66;
                  }

                  for (int j = 0; j < len; j++) {
                     if (!expr.accept(state, currentSubst.charAt(j))) {
                        continue label66;
                     }
                  }
               }
            }

            if (state.isFinal() && this.checkCase(cstate, tableWord.caseCorrection)) {
               return true;
            }
         }
      }

      return false;
   }

   private int adjustWordForSubstitutionLengthAndCaseCorrection(String substitution, SimplePrefixTable$SimplePrefixTableWord tableWord) {
      int subLen = substitution.length();
      if (substitution.charAt(subLen - 1) < super.iReader._caseControlCount) {
         tableWord.caseCorrection = substitution.charAt(--subLen);
      }

      return subLen;
   }

   private void adjustCase(int caseCorrection, char[] buff, int buffIndex, char firstChar, char secondChar, Locale locale) {
      switch (caseCorrection) {
         case 0:
            buff[0] = firstChar;
            buff[1] = secondChar;
            break;
         case 1:
         default:
            buff[0] = CaseCorrector.toUpperCase(firstChar, locale);
            buff[1] = secondChar;
            return;
         case 2:
            for (int j = 0; j < buffIndex; j++) {
               buff[j] = CaseCorrector.toUpperCase(buff[j], locale);
            }
            break;
         case 3:
            buff[0] = firstChar;
            buff[1] = CaseCorrector.toUpperCase(secondChar, locale);
            return;
         case 4:
            buff[0] = CaseCorrector.toUpperCase(firstChar, locale);
            buff[1] = CaseCorrector.toUpperCase(secondChar, locale);
            return;
      }
   }

   private boolean checkCase(ComplexTableRegularExpressionState cstate, char caseCorrection) {
      if (caseCorrection != 0) {
         if (cstate != null) {
            switch (caseCorrection) {
               case '\u0000':
                  break;
               case '\u0001':
               default:
                  if (!cstate.checkCorrectCasePrefix(true, false)) {
                     return false;
                  }
                  break;
               case '\u0002':
               case '\u0004':
                  if (!cstate.checkCorrectCasePrefix(true, true)) {
                     return false;
                  }
                  break;
               case '\u0003':
                  if (!cstate.checkCorrectCasePrefix(false, true)) {
                     return false;
                  }
            }
         }
      } else if (!cstate.checkCorrectCasePrefix(false, false)) {
         return false;
      }

      return true;
   }

   private boolean insertWord(char[] aBuffer, int aBufferLen, int aValidWordLen, ReIterator aPrefix, boolean aIsCapitalPair, ResultContainer aRes) {
      ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
      char freq = this.nextFreq();
      insertedWord.setData(aBuffer, 0, aBufferLen, freq);
      this.correctCase(insertedWord);
      boolean isValidWord = aValidWordLen == -1 ? true : insertedWord._length == aValidWordLen;
      insertedWord.setValidWord(isValidWord);
      boolean ret = aRes.insert(insertedWord, 0, aPrefix, 1);
      if (aIsCapitalPair && isValidWord) {
         insertedWord._frequency = this.nextFreq();
         insertedWord._variants[0] = CaseCorrector.toUpperCase(insertedWord._variants[0], Locale.getDefaultInputForSystem());
         ret = aRes.insert(insertedWord, 0, aPrefix, 1) || ret;
      }

      return ret || !aRes.isFull() || freq > (aRes.getMinFrequency() & 65535);
   }
}
