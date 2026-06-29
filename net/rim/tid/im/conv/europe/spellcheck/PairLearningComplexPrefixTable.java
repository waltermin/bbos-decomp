package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.LearningComplexPrefixTable;
import net.rim.tid.im.conv.europe.repository.LearningReader;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;

public class PairLearningComplexPrefixTable extends LearningComplexPrefixTable {
   PairLearningPrefixTableHelper _helper = new PairLearningPrefixTableHelper();

   public PairLearningComplexPrefixTable(int aLevel, LearningReader aReader) {
      super(aLevel, aReader);
   }

   @Override
   protected int addTable(byte[] aEncodedPair, int aKeyLength, int aReplacementLength, int aStart, boolean aIsExactMatch) {
      int fileSize = super._reader.getFileSize();
      int length;
      int pairLength;
      if (aIsExactMatch) {
         length = 1;
         pairLength = aReplacementLength + 1;
      } else {
         length = 4;
         pairLength = aKeyLength + aReplacementLength + 1 - super._prefixLength - 1;
      }

      length += pairLength;
      if (aStart <= fileSize) {
         System.arraycopy(super._wordsDefTable, aStart, super._wordsDefTable, aStart + length, fileSize - aStart);
      }

      int offset = aStart;
      if (aIsExactMatch) {
         super._wordsDefTable[offset++] = (byte)length;
      } else {
         super._wordsDefTable[offset++] = 0;
         super._wordsDefTable[offset++] = (byte)(length - 2);
         super._wordsDefTable[offset++] = 1;
         super._wordsDefTable[offset++] = (byte)(length - 3);
      }

      System.arraycopy(aEncodedPair, super._prefixLength + 1, super._wordsDefTable, offset, pairLength);
      return length;
   }

   @Override
   protected int initExactMatch(int aOffset) {
      int next_offset = super._wordsDefTable[aOffset] & 255;
      super._exactMatchStart++;
      return aOffset + next_offset;
   }

   @Override
   protected void addExactMatch(byte[] aEncoded, int aKeyLength, int aReplacementLength, byte aPriority) {
      if (super._hasExactMatch) {
         super._wordsDefTable[super._exactMatchStart] = (byte)((super._wordsDefTable[super._exactMatchStart] & 127) + 1 | 128);
         super._wordsDefTable[super._localAlphabetStart - 1] = (byte)(super._wordsDefTable[super._localAlphabetStart - 1] | 128);
      } else {
         int grow = this.addTable(aEncoded, aKeyLength, aReplacementLength, super._exactMatchStart, true);
         this.growFileSize(grow);
      }
   }

   @Override
   public void readAndInsertExactMatch(ReIterator aPrefix, int aOffset, int aNextOffset, ResultContainer aRes, char[] aBuffer) {
      this._helper.readAndInsertPairs(super._reader, super._wordsDefTable, aBuffer, super._prefixLength, aOffset, aNextOffset, aRes);
   }
}
