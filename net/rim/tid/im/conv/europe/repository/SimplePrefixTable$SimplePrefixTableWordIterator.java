package net.rim.tid.im.conv.europe.repository;

class SimplePrefixTable$SimplePrefixTableWordIterator {
   private int _nextIndex;
   private int _wordIndex;
   private final SimplePrefixTable this$0;

   SimplePrefixTable$SimplePrefixTableWordIterator(SimplePrefixTable _1) {
      this.this$0 = _1;
   }

   void reset() {
      this._nextIndex = this.this$0.iWordsDataOffset;
      this._wordIndex = 0;
   }

   boolean hasNext() {
      return this._wordIndex < this.this$0.iWordsCount;
   }

   void next(SimplePrefixTable$SimplePrefixTableWord word) {
      word.start = this._nextIndex;
      int i = this._nextIndex;
      byte[] dt = this.this$0.iWordsDefTable;

      int coded;
      do {
         coded = dt[i++];
      } while ((coded & 128) == 0);

      coded &= 127;
      if (coded < this.this$0.iReader._caseControlCount) {
         word.end = i - 1;
         word.caseCorrection = (char)coded;
         if (coded == 5) {
            this._wordIndex++;
         }
      } else {
         word.end = i;
         word.caseCorrection = 0;
      }

      this._nextIndex = i;
      this._wordIndex++;
   }
}
