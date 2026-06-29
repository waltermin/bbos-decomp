package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashState {
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashState(PhoneNumberPatternAnalyzer _1) {
      this.this$0 = _1;
   }

   protected void process(char c, int index) {
      if (PhoneNumberPatternAnalyzer.isDigit(c)) {
         this.this$0.STATE_DIGIT.setCurrent(c, index);
      } else if (PhoneNumberPatternAnalyzer.isWhitespace(c)) {
         this.this$0.STATE_SPACE.setCurrent(c);
      } else {
         switch (c) {
            case '#':
            case '(':
            case ')':
            case '*':
            case '+':
               this.this$0.updateHashValue(c);
               this.this$0.logCandidate(index, this.this$0._hashVal, this.this$0._digitCount);
               this.this$0.STATE_EAT_SPACES.setCurrent('\u0000');
               return;
            default:
               this.this$0.STATE_DONE.setCurrent();
         }
      }
   }
}
