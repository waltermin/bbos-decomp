package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashStateDigitSpace extends PhoneNumberPatternAnalyzer$HashState {
   private int _pendingIndex;
   private char _firstWhitespaceChar;
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashStateDigitSpace(PhoneNumberPatternAnalyzer _1) {
      super(_1);
      this.this$0 = _1;
   }

   public void setCurrent(char firstCharInRun, int lastIndexOfDigitsRun) {
      this._firstWhitespaceChar = firstCharInRun;
      this._pendingIndex = lastIndexOfDigitsRun;
      this.this$0._state = this;
   }

   @Override
   public void process(char c, int index) {
      if (PhoneNumberPatternAnalyzer.isDigit(c)) {
         if (this.this$0._appending) {
            this.this$0.updateHashValue(45);
            this.this$0.STATE_DIGIT.setCurrent(c, index);
         } else {
            this.this$0.updateHashValue(45);
            this.this$0.STATE_DIGIT.setCurrent(c, index);
         }
      } else if (PhoneNumberPatternAnalyzer.isWhitespace(c)) {
         this.this$0.logCandidate(this._pendingIndex, this.this$0._hashVal, this.this$0._digitCount);
         this.this$0.STATE_SPACE.setCurrent(this._firstWhitespaceChar);
         this.this$0._state.process(c, index);
      } else {
         this.this$0.logCandidate(this._pendingIndex, this.this$0._hashVal, this.this$0._digitCount);
         super.process(c, index);
      }
   }
}
