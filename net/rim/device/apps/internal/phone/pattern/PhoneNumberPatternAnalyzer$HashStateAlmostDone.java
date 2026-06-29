package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashStateAlmostDone extends PhoneNumberPatternAnalyzer$HashState {
   private int _pendingIndex;
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashStateAlmostDone(PhoneNumberPatternAnalyzer _1) {
      super(_1);
      this.this$0 = _1;
   }

   public void setCurrent(int pendingIndex) {
      this._pendingIndex = pendingIndex;
      this.this$0._state = this;
   }

   @Override
   public void process(char c, int index) {
      if (!PhoneNumberPatternAnalyzer.isDigit(c)) {
         this.this$0.logCandidate(this._pendingIndex, this.this$0._hashVal, this.this$0._digitCount);
      }

      this.this$0.STATE_DONE.setCurrent();
   }
}
