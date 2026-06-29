package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.Utils;

public class ComplexPrefixTable extends PrefixTable {
   protected int iHotWords = 0;
   protected int iHotWordsIndexesStart;
   protected int iAlphLength;
   protected int iLocalAlphabetStart;
   protected int iSubTablesLength;
   protected int iSubTablesStart;
   protected int iExceptionsStart;
   protected int iIndexesStart;
   protected boolean iHasExactMatch;

   public ComplexPrefixTable(int aLevel, Reader aReader) {
      super(aLevel, aReader);
   }

   @Override
   public void init(byte[] aWordsDefTable, int aOffset, char aLastPrefixChar) {
      super.iLastPrefixChar = aLastPrefixChar;
      super.iWordsDefTable = aWordsDefTable;
      if (super.iWordsDefTable[aOffset] < 0) {
         this.iHotWords = super.iWordsDefTable[aOffset++] & 127;
         this.iHasExactMatch = true;
      } else {
         this.iHotWords = super.iWordsDefTable[aOffset++] & 255;
         this.iHasExactMatch = false;
      }

      this.iHotWordsIndexesStart = aOffset;
      aOffset += this.iHotWords * 2;
      this.iAlphLength = super.iWordsDefTable[aOffset++] & 255;
      this.iLocalAlphabetStart = aOffset;
      aOffset += this.iAlphLength;
      this.iIndexesStart = aOffset;
      aOffset += this.iAlphLength * 2;
      this.iSubTablesLength = Utils.bytes2Int(super.iWordsDefTable[aOffset++], aWordsDefTable[aOffset++]);
      this.iSubTablesStart = aOffset;
      this.iExceptionsStart = aOffset + this.iSubTablesLength;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void getMatches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate, char[] buff, ResultContainer result) {
      if (this.iHasExactMatch && state.isFinal()) {
         ExtendedCurrentVariant insertedWord = result.getTempInsertedWordContainer();
         insertedWord.setData(buff, 0, super.iPrefixLength, 0);
         result.insertWord(insertedWord);
         if (result.isFull()) {
            return;
         }
      }

      RegularExpression$SimpleCharacterIterator acceptable = expr.getAcceptableChars(state, super.iReader.iAlphabet);
      boolean var14 = false /* VF: Semaphore variable */;

      label64: {
         try {
            var14 = true;
            Object var16 = state.newMark();

            while (acceptable.hasNext()) {
               char ch = acceptable.next();
               if (expr.accept(state, ch)) {
                  int code = super.iReader.iAlphabetLookup.get(ch);
                  if (code != -1) {
                     int position = this.getIndexInAlphabet(code);
                     if (position != -1) {
                        PrefixTable table = this.getTable(position, ch);
                        buff[super.iPrefixLength] = ch;
                        table.getMatches(expr, state, cstate, buff, result);
                        if (result.isFull()) {
                           var14 = false;
                           break label64;
                        }
                     }
                  }

                  state.rollback(var16);
               }
            }

            var14 = false;
         } finally {
            if (var14) {
               acceptable.close();
            }
         }

         acceptable.close();
         return;
      }

      acceptable.close();
   }

   @Override
   public boolean matches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate) {
      if (this.iHasExactMatch && state.isFinal()) {
         return true;
      }

      RegularExpression$SimpleCharacterIterator acceptable = expr.getAcceptableChars(state, super.iReader.iAlphabet);

      while (acceptable.hasNext()) {
         Object mark = state.newMark();
         char ch = acceptable.next();
         if (expr.accept(state, ch)) {
            int code = super.iReader.iAlphabetLookup.get(ch);
            if (code != -1) {
               int position = this.getIndexInAlphabet(code);
               if (position != -1) {
                  PrefixTable table = this.getTable(position, ch);
                  if (table.matches(expr, state, cstate)) {
                     return true;
                  }
               }
            }

            state.rollback(mark);
         }
      }

      return false;
   }

   public void getPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      aBuffer[super.iPrefixLength - 1] = super.iLastPrefixChar;
      if (super.iPrefixLength == 2) {
         aBuffer[0] = aPrefix.getRe()[0];
      }

      int len = aPrefix.getLength();
      boolean isNonFreqSpellCheck = aRes.isSpellCheck() && !super.iReader.isFrequencyIncluded();
      if (len == super.iPrefixLength) {
         if (isNonFreqSpellCheck) {
            this.getExactPredictions(aPrefix, aRes, aBuffer);
            return;
         }

         if (!aPrefix.hasQuantifiers()) {
            this.getHotPredictions(aPrefix, aRes, aBuffer);
            return;
         }
      } else if (isNonFreqSpellCheck && aRes.getMaxWordLength() <= super.iPrefixLength) {
         return;
      }

      char ch = aPrefix.nextChar();
      if (ch == 4) {
         int end = this.iAlphLength + this.iLocalAlphabetStart;

         for (int i = this.iLocalAlphabetStart; i < end; i++) {
            char ch1 = super.iReader.iAlphabet.charAt(super.iWordsDefTable[i]);
            this.getPredictionsRecursively(aPrefix, aRes, aBuffer, i - this.iLocalAlphabetStart, ch1);
         }
      } else {
         boolean next_case = true;

         while (ch != 0) {
            int code = super.iReader.iAlphabetLookup.get(ch);
            if (code != -1) {
               int position = this.getIndexInAlphabet(code);
               if (position != -1) {
                  this.getPredictionsRecursively(aPrefix, aRes, aBuffer, position, ch);
               }
            }

            if (next_case && !aPrefix.isCaseSensitive()) {
               ch = CharacterUtilities.isUpperCase(ch) ? Utils.toLowerCase(ch) : Utils.toUpperCase(ch);
               next_case = false;
            } else {
               ch = aPrefix.nextChar();
               next_case = true;
            }
         }
      }
   }

   private void getPredictionsRecursively(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer, int aPosition, char aChar) {
      aPrefix.pushState();
      int index = this.getOffsetFor(aPosition);
      if ((index & 32768) != 0) {
         ComplexPrefixTable table = super.iReader.iComplexPrefixTables[super.iPrefixLength - 1];
         table.init(super.iWordsDefTable, this.iSubTablesStart + (index & -32769), aChar);
         aPrefix.nextWildcard();
         table.getPredictions(aPrefix, aRes, aBuffer);
      } else {
         SimplePrefixTable table = super.iReader.getSimplePrefixTable(super.iPrefixLength + 1);
         table.init(super.iWordsDefTable, this.iSubTablesStart + (index & -32769), aChar);
         aPrefix.nextWildcard();
         table.getPredictions(aPrefix, aRes, aBuffer);
      }

      aPrefix.popState();
   }

   public void getHotPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      if (aRes.isFast() && !aRes.isPredictive() && this.iHasExactMatch) {
         SimplePrefixTable swr = super.iReader.getSimplePrefixTable(super.iPrefixLength);
         swr.init(super.iWordsDefTable, this.iExceptionsStart, super.iLastPrefixChar);
         swr.getFirstWordsForFastRegular(1, aPrefix, true, aRes, aBuffer);
      } else {
         int end = this.iHotWordsIndexesStart + this.iHotWords * 2;

         for (int i = this.iHotWordsIndexesStart; i < end; i += 2) {
            int ind1 = super.iWordsDefTable[i] & 255;
            int count = super.iWordsDefTable[i + 1] & 255;
            if (ind1 < 127) {
               SimplePrefixTable swr = super.iReader.getSimplePrefixTable(super.iPrefixLength + 1);
               int index = Utils.bytes2Int(super.iWordsDefTable[this.iIndexesStart + ind1 * 2], super.iWordsDefTable[this.iIndexesStart + ind1 * 2 + 1]);
               char ch = super.iReader.iAlphabet.charAt(super.iWordsDefTable[this.iLocalAlphabetStart + ind1]);
               swr.init(super.iWordsDefTable, this.iSubTablesStart + index, ch);
               aBuffer[super.iPrefixLength] = ch;
               swr.getFirstWords(aPrefix, super.iPrefixLength, count, false, aRes, aBuffer);
            } else {
               SimplePrefixTable swr = super.iReader.getSimplePrefixTable(super.iPrefixLength);
               swr.init(super.iWordsDefTable, this.iExceptionsStart, super.iLastPrefixChar);
               swr.getFirstWords(aPrefix, super.iPrefixLength, count, true, aRes, aBuffer);
            }
         }
      }
   }

   public int getIndexInAlphabet(int code) {
      int end = this.iAlphLength + this.iLocalAlphabetStart;

      for (int i = this.iLocalAlphabetStart; i < end; i++) {
         if (super.iWordsDefTable[i] == code) {
            return i - this.iLocalAlphabetStart;
         }
      }

      return -1;
   }

   public int getOffsetFor(int aIndex) {
      aIndex *= 2;
      return Utils.bytes2Int(super.iWordsDefTable[this.iIndexesStart + aIndex], super.iWordsDefTable[this.iIndexesStart + aIndex + 1]);
   }

   private void getExactPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      int end = this.iHotWordsIndexesStart + this.iHotWords * 2;
      int total_count = 0;
      int exception_count = 0;
      int exception_id = -1;

      for (int i = this.iHotWordsIndexesStart; i < end; i += 2) {
         int ind1 = super.iWordsDefTable[i] & 255;
         int count = super.iWordsDefTable[i + 1] & 255;
         if (ind1 >= 127) {
            exception_count += count;
            exception_id = total_count;
         }

         total_count += count;
      }

      if (exception_count != 0) {
         int subst_index = aPrefix.getCurrentWildcardSubstIndex();
         SimplePrefixTable swr = super.iReader.getSimplePrefixTable(super.iPrefixLength);
         swr.init(super.iWordsDefTable, this.iExceptionsStart, super.iLastPrefixChar, total_count - exception_id, total_count);
         swr.getFirstWords(aPrefix, super.iPrefixLength, exception_count, true, aRes, aBuffer);
      }
   }

   private PrefixTable getTable(int position, char ch) {
      int index = this.getOffsetFor(position);
      PrefixTable table;
      if ((index & 32768) != 0) {
         table = super.iReader.iComplexPrefixTables[super.iPrefixLength - 1];
         table.init(super.iWordsDefTable, this.iSubTablesStart + (index & -32769), ch);
      } else {
         table = super.iReader.getSimplePrefixTable(super.iPrefixLength + 1);
         table.init(super.iWordsDefTable, this.iSubTablesStart + index, ch);
      }

      return table;
   }
}
