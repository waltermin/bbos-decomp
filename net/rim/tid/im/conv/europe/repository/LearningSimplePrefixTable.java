package net.rim.tid.im.conv.europe.repository;

public class LearningSimplePrefixTable extends LearningPrefixTable {
   protected int _wordsCount;
   protected int _wordsDataOffset;

   public LearningSimplePrefixTable(int aLevel, LearningReader aReader) {
      super(aLevel, aReader);
   }

   @Override
   public void init(byte[] aWordsDefTable, int aOffset, char aLastPrefixChar, LearningPrefixTable aParent, int aSize) {
      super._lastPrefixChar = aLastPrefixChar;
      super._wordsDefTable = aWordsDefTable;
      this._wordsCount = super._wordsDefTable[aOffset] & 255;
      this._wordsDataOffset = aOffset + 1;
      super._parent = aParent;
      super._size = aSize;
   }

   @Override
   public void growFileSize(int aGrow) {
      super._size += aGrow;
      super._wordsDefTable[this._wordsDataOffset - 3] = (byte)(super._size >>> 8);
      super._wordsDefTable[this._wordsDataOffset - 2] = (byte)super._size;
      super._parent.growFileSize(aGrow);
   }

   @Override
   public boolean removeWord(byte[] aEncodedWord, int aLength) {
      return false;
   }

   @Override
   public int getEntryNo() {
      return this._wordsCount;
   }

   protected int getSplitSize() {
      return super._reader.getSplitSize();
   }
}
