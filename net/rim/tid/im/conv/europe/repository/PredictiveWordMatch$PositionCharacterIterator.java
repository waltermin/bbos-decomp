package net.rim.tid.im.conv.europe.repository;

class PredictiveWordMatch$PositionCharacterIterator implements RegularExpression$SimpleCharacterIterator {
   int[] positions;
   int totalPositions;
   int currPosition;
   int lowerIndex;
   private final PredictiveWordMatch this$0;

   PredictiveWordMatch$PositionCharacterIterator(PredictiveWordMatch _1) {
      this.this$0 = _1;
      this.positions = new int[5];
      this.totalPositions = 0;
   }

   void reset() {
      this.totalPositions = 0;
      this.currPosition = 0;
      this.lowerIndex = 0;
   }

   void addPosition(int position) {
      for (int i = 0; i < this.totalPositions; i++) {
         if (position == this.positions[i]) {
            return;
         }
      }

      this.positions[this.totalPositions++] = position;
   }

   @Override
   public boolean hasNext() {
      if (this.currPosition < this.totalPositions) {
         return this.lowerIndex < this.this$0.lower.length ? true : this.currPosition + 1 < this.totalPositions;
      } else {
         return false;
      }
   }

   @Override
   public char next() {
      if (this.lowerIndex < this.this$0.lower.length) {
         return this.this$0.lower[this.lowerIndex++][this.positions[this.currPosition]];
      }

      this.lowerIndex = 0;
      return this.this$0.lower[this.lowerIndex++][this.positions[++this.currPosition]];
   }

   @Override
   public void close() {
      this.this$0.iterators.push(this);
   }
}
