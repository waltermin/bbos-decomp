package net.rim.tid.im.conv.europe.repository;

import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.io.ContinuousByteArray;
import net.rim.tid.io.ContinuousByteArray$Position;

class PrefixTable {
   protected Reader iReader;
   protected byte[] iWordsDefTable;
   protected char iLastPrefixChar;
   protected int iPrefixLength;

   PrefixTable(int aLevel, Reader aReader) {
      this.iPrefixLength = aLevel;
      this.iReader = aReader;
   }

   public void getMatches(RegularExpression _1, RegularExpressionState _2, ComplexTableRegularExpressionState _3, char[] _4, ResultContainer _5) {
      throw null;
   }

   public boolean matches(RegularExpression _1, RegularExpressionState _2, ComplexTableRegularExpressionState _3) {
      throw null;
   }

   public void init(ContinuousByteArray aWordsDefTable, int aOffset, char aLastPrefixChar) {
      ContinuousByteArray$Position pos = aWordsDefTable.getPosition(aOffset);
      this.init(pos.iBlock, pos.iOffset, aLastPrefixChar);
   }

   void init(byte[] _1, int _2, char _3) {
      throw null;
   }

   protected int readAndInsertWord(
      int aOffset, char aFreq, ResultContainer aRes, int aPrefLength, int aBufIndex, char[] aBuffer, ReIterator aPrefix, boolean aInsertExactMatchOnly
   ) {
      int buf_index = aBufIndex;
      byte coded = 0;
      int offset = aOffset;
      boolean skip = false;
      String alphabet = this.iReader.getAlphabet();
      int threshold = aInsertExactMatchOnly ? this.iReader._caseControlCount : this.iReader.iSubstShift;

      do {
         coded = this.iWordsDefTable[offset++];
         if (!skip) {
            int cod = coded & 127;
            if (cod < threshold) {
               if (buf_index > aRes.getMaxWordLength()) {
                  skip = true;
               } else {
                  char ch = alphabet.charAt(cod);
                  if (ch != 0) {
                     aBuffer[buf_index++] = ch;
                  }
               }
            } else if (aInsertExactMatchOnly) {
               skip = true;
            } else {
               String subst = this.iReader.iSubstTable.getSubstitution(cod, this.iReader.iSubstShift);
               int len = subst.length();
               if (buf_index + len - 1 > aRes.getMaxWordLength()) {
                  skip = true;
               } else {
                  for (int j = 0; j < len; j++) {
                     aBuffer[buf_index++] = subst.charAt(j);
                  }
               }
            }
         }
      } while ((coded & 128) == 0);

      if (!skip) {
         ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
         insertedWord.setData(aBuffer, 0, buf_index, aFreq);
         this.correctCase(insertedWord);
         insertedWord.setValidWord(insertedWord._length == aPrefLength);
         aRes.insert(insertedWord, aPrefLength, aPrefix, 1);
      }

      return offset;
   }

   protected void correctCase(SLCurrentVariant aWord) {
      this.iReader.correctCase(aWord);
   }
}
