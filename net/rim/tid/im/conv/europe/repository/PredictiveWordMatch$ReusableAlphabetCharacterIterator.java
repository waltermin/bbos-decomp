package net.rim.tid.im.conv.europe.repository;

class PredictiveWordMatch$ReusableAlphabetCharacterIterator extends RegularExpression$AlphabetCharacterIterator {
   private final PredictiveWordMatch this$0;

   PredictiveWordMatch$ReusableAlphabetCharacterIterator(PredictiveWordMatch _1, LearningGlobalAlphabet alpha) {
      super(alpha);
      this.this$0 = _1;
   }

   @Override
   protected void setAlphabet(LearningGlobalAlphabet alpha) {
      super.setAlphabet(alpha);
   }

   @Override
   public void close() {
      this.this$0.alphaIterators.push(this);
   }
}
