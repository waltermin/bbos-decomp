package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashStateDone extends PhoneNumberPatternAnalyzer$HashState {
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashStateDone(PhoneNumberPatternAnalyzer _1) {
      super(_1);
      this.this$0 = _1;
   }

   public void setCurrent() {
      this.this$0._state = this;
   }

   @Override
   public void process(char c, int index) {
   }
}
