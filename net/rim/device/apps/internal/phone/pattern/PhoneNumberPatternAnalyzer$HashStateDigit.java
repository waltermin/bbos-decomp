package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashStateDigit extends PhoneNumberPatternAnalyzer$HashState {
   private int _pendingIndex;
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashStateDigit(PhoneNumberPatternAnalyzer _1) {
      super(_1);
      this.this$0 = _1;
   }

   public void setCurrent(int indexOfFirstChar) {
      this._pendingIndex = indexOfFirstChar;
      this.this$0._state = this;
   }

   public void setCurrent(char firstCharInRun, int indexOfFirstChar) {
      this.this$0.updateHashValue(48);
      PhoneNumberPatternAnalyzer.access$408(this.this$0);
      this._pendingIndex = indexOfFirstChar;
      this.this$0._state = this;
   }

   @Override
   public void process(char c, int index) {
      if (PhoneNumberPatternAnalyzer.isWhitespace(c)) {
         this.this$0.STATE_DIGIT_SPACE.setCurrent(c, this._pendingIndex);
      } else {
         if (PhoneNumberPatternAnalyzer.isDigit(c)) {
            this.this$0.updateHashValue(48);
            PhoneNumberPatternAnalyzer.access$408(this.this$0);
            this._pendingIndex = index;
            if (this.this$0._digitCount > 40) {
               this.this$0.STATE_DONE.setCurrent();
               return;
            }
         } else {
            if (PhoneNumberPatternAnalyzer.isAlpha(c)) {
               if (this.this$0._appending) {
                  this.this$0.STATE_DIGIT_EXT.setCurrent(c, this._pendingIndex);
                  return;
               }

               this.this$0.logCandidate(this._pendingIndex, this.this$0._hashVal, this.this$0._digitCount);
               super.process(c, index);
               return;
            }

            switch (c) {
               case '$':
               case '%':
               case '¢':
               case '£':
               case '¥':
               case '€':
                  super.process(c, index);
                  return;
               case '+':
                  super.process(c, index);
                  return;
               case ':':
                  this.this$0.STATE_ALMOST_DONE.setCurrent(this._pendingIndex);
                  return;
               default:
                  this.this$0.logCandidate(this._pendingIndex, this.this$0._hashVal, this.this$0._digitCount);
                  super.process(c, index);
            }
         }
      }
   }
}
