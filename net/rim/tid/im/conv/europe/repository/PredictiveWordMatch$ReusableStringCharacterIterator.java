package net.rim.tid.im.conv.europe.repository;

class PredictiveWordMatch$ReusableStringCharacterIterator extends RegularExpression$StringCharacterIterator {
   private final PredictiveWordMatch this$0;

   PredictiveWordMatch$ReusableStringCharacterIterator(PredictiveWordMatch _1, String str) {
      super(str);
      this.this$0 = _1;
   }

   @Override
   protected void setString(String str) {
      super.setString(str);
   }

   @Override
   public void close() {
      this.this$0.strIterators.push(this);
   }
}
