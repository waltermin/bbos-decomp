package net.rim.tid.im.conv.europe.spellcheck;

class PairLearningSimplePrefixTable$SimplePrefixTableWordIterator {
   private int _nextIndex;
   private int _wordIndex;
   private final PairLearningSimplePrefixTable this$0;

   PairLearningSimplePrefixTable$SimplePrefixTableWordIterator(PairLearningSimplePrefixTable _1) {
      this.this$0 = _1;
   }

   void reset() {
      this._nextIndex = PairLearningSimplePrefixTable.access$200(this.this$0);
      this._wordIndex = 0;
   }

   boolean hasNext() {
      return this._wordIndex < PairLearningSimplePrefixTable.access$300(this.this$0);
   }

   void next(PairLearningSimplePrefixTable$SimplePrefixTableWord word) {
      int nextWord = this._nextIndex + (PairLearningSimplePrefixTable.access$400(this.this$0)[this._nextIndex] & 255);
      this._nextIndex++;
      word.start = this._nextIndex;
      int i = this._nextIndex;

      int coded;
      do {
         coded = PairLearningSimplePrefixTable.access$500(this.this$0)[i++];
      } while ((coded & 128) == 0);

      word.end = i;
      word.pairedStart = i + 1;
      word.pairedEnd = nextWord;
      this._nextIndex = nextWord;
      this._wordIndex++;
   }
}
