package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashStateEatSpaces extends PhoneNumberPatternAnalyzer$HashState {
   private char _prevChar;
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashStateEatSpaces(PhoneNumberPatternAnalyzer _1) {
      super(_1);
      this.this$0 = _1;
   }

   public void setCurrent(char firstCharInRun) {
      this._prevChar = firstCharInRun;
      this.this$0._state = this;
   }

   @Override
   public void process(char c, int index) {
      if (PhoneNumberPatternAnalyzer.isWhitespace(c)) {
         if (c == this._prevChar) {
            this.this$0.STATE_DONE.setCurrent();
         } else {
            this._prevChar = c;
         }
      } else {
         super.process(c, index);
      }
   }
}
