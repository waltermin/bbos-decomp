package net.rim.device.apps.internal.phone.pattern;

class PhoneNumberPatternAnalyzer$HashStateDigitExt extends PhoneNumberPatternAnalyzer$HashState {
   private int _pendingIndex;
   private char _prevLetter;
   private int _prevPosition;
   private final PhoneNumberPatternAnalyzer this$0;

   PhoneNumberPatternAnalyzer$HashStateDigitExt(PhoneNumberPatternAnalyzer _1) {
      super(_1);
      this.this$0 = _1;
   }

   public void setCurrent(char firstLetter, int pendingDigitIndex) {
      this._pendingIndex = pendingDigitIndex;
      this._prevLetter = firstLetter;
      this._prevPosition = 0;
      if (this.isValidSequence()) {
         this.this$0._state = this;
      } else {
         this.this$0.STATE_DONE.setCurrent();
      }
   }

   private boolean isValidSequence() {
      switch (this._prevLetter) {
         case 'E':
         case 'e':
            if (this._prevPosition == 0) {
               return true;
            }

            return false;
         case 'T':
         case 't':
            if (this._prevPosition == 2) {
               return true;
            }

            return false;
         case 'X':
         case 'x':
            if (this._prevPosition != 0 && this._prevPosition != 1) {
               return false;
            }

            return true;
         default:
            return false;
      }
   }

   private boolean isTerminalSequence() {
      switch (this._prevLetter) {
         case 'T':
         case 't':
            if (this._prevPosition == 2) {
               return true;
            }

            return false;
         case 'X':
         case 'x':
            if (this._prevPosition == 0) {
               return true;
            }

            return false;
         default:
            return false;
      }
   }

   @Override
   public void process(char c, int index) {
      if (!PhoneNumberPatternAnalyzer.isDigit(c) && !PhoneNumberPatternAnalyzer.isWhitespace(c) && c != '(' && c != ')') {
         if (PhoneNumberPatternAnalyzer.isAlpha(c)) {
            this._prevLetter = c;
            this._prevPosition++;
            if (!this.isValidSequence()) {
               this.this$0.STATE_DONE.setCurrent();
               return;
            }
         } else {
            this.this$0.STATE_DONE.setCurrent();
         }
      } else {
         if (this.isTerminalSequence()) {
            this.this$0.logCandidate(this._pendingIndex, this.this$0._hashVal, this.this$0._digitCount);
         }

         this.this$0.STATE_DONE.setCurrent();
      }
   }
}
