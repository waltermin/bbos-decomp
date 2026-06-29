package net.rim.tid.im.conv.europe.repository;

class WordLearningSimplePrefixTable$SimplePrefixTableWordIterator {
   private int _nextIndex;
   private int _wordIndex;
   private final WordLearningSimplePrefixTable this$0;

   WordLearningSimplePrefixTable$SimplePrefixTableWordIterator(WordLearningSimplePrefixTable _1) {
      this.this$0 = _1;
   }

   void reset() {
      this._nextIndex = this.this$0._wordsDataOffset + 1 + this.this$0._wordsCount;
      this._wordIndex = 0;
   }

   boolean hasNext() {
      return this._wordIndex < this.this$0._wordsCount;
   }

   void next(WordLearningSimplePrefixTable$SimplePrefixTableWord word) {
      word.start = this._nextIndex;
      if (this.this$0._skipFreqSize != 0) {
         word.freq = (char)(this.this$0._wordsDefTable[this._nextIndex] << 8 | this.this$0._wordsDefTable[this._nextIndex + 1] & 0xFF);
         this._nextIndex = this._nextIndex + this.this$0._skipFreqSize;
      } else {
         word.freq = 0;
      }

      int i = this._nextIndex;

      int coded;
      do {
         coded = this.this$0._wordsDefTable[i++];
      } while ((coded & 128) == 0);

      coded &= 127;
      if (coded < 5) {
         word.end = i - 1;
         word.caseCorrection = (char)coded;
      } else {
         word.end = i;
         word.caseCorrection = 0;
      }

      this._nextIndex = i;
      this._wordIndex++;
   }
}
