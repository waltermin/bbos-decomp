package net.rim.device.apps.internal.phone.pattern;

final class PhoneNumberPatternAnalyzer {
   private PhoneNumberPatternAnalyzer$HashStateInit STATE_INIT = new PhoneNumberPatternAnalyzer$HashStateInit(this);
   private PhoneNumberPatternAnalyzer$HashStateEatSpaces STATE_EAT_SPACES = new PhoneNumberPatternAnalyzer$HashStateEatSpaces(this);
   private PhoneNumberPatternAnalyzer$HashStateSpace STATE_SPACE = new PhoneNumberPatternAnalyzer$HashStateSpace(this);
   private PhoneNumberPatternAnalyzer$HashStateDigit STATE_DIGIT = new PhoneNumberPatternAnalyzer$HashStateDigit(this);
   private PhoneNumberPatternAnalyzer$HashStateDigitSpace STATE_DIGIT_SPACE = new PhoneNumberPatternAnalyzer$HashStateDigitSpace(this);
   private PhoneNumberPatternAnalyzer$HashStateDigitExt STATE_DIGIT_EXT = new PhoneNumberPatternAnalyzer$HashStateDigitExt(this);
   private PhoneNumberPatternAnalyzer$HashStateAlmostDone STATE_ALMOST_DONE = new PhoneNumberPatternAnalyzer$HashStateAlmostDone(this);
   private PhoneNumberPatternAnalyzer$HashStateDone STATE_DONE = new PhoneNumberPatternAnalyzer$HashStateDone(this);
   private PhoneNumberPatternAnalyzer$Logger _logger;
   private PhoneNumberPatternAnalyzer$HashState _state;
   private int _firstCharIndex;
   private boolean _appending;
   private int _hashScale;
   private int _hashPower;
   private int _hashVal;
   private int _digitCount;
   private static final int MAX_DIGIT_COUNT;

   public PhoneNumberPatternAnalyzer() {
      this(null);
   }

   public PhoneNumberPatternAnalyzer(PhoneNumberPatternAnalyzer$Logger logger) {
      this._logger = logger;
      this.reset();
   }

   public final void reset() {
      this._hashVal = 0;
      this._hashScale = 1;
      this._hashPower = 0;
      this._digitCount = 0;
      this._firstCharIndex = -1;
      this.STATE_INIT.setCurrent();
   }

   public final void reset(int hashVal, int digitCount) {
      this._hashVal = hashVal;
      this._hashScale = 1;
      this._hashPower = 0;
      this._digitCount = digitCount;
      if (this._firstCharIndex < 0) {
         this.STATE_INIT.setCurrent();
      } else {
         this.STATE_DIGIT.setCurrent(this._firstCharIndex);
      }
   }

   public final void prepend(char c, int index) {
      this._appending = false;
      this._state.process(c, index);
   }

   public final void append(char c, int index) {
      this._appending = true;
      this._state.process(c, index);
   }

   public final boolean isDone() {
      return this._state == this.STATE_DONE;
   }

   public final int calculateHashValue(StringBuffer pattern) {
      this.reset();
      int idxMax = pattern.length();

      for (int idx = 0; idx < idxMax; idx++) {
         this.append(pattern.charAt(idx), idx);
      }

      return this.getCurrentHashValue();
   }

   public final int calculateHashValue(String pattern) {
      this.reset();
      int idxMax = pattern.length();

      for (int idx = 0; idx < idxMax; idx++) {
         this.append(pattern.charAt(idx), idx);
      }

      return this.getCurrentHashValue();
   }

   public final int getCurrentHashValue() {
      return this._hashVal;
   }

   public final int getCurrentDigitCount() {
      return this._digitCount;
   }

   private final void updateHashValue(int hashChar) {
      if (this._appending) {
         this._hashVal = this._hashVal * 31 + hashChar;
      } else {
         this._hashVal = this._hashVal + this._hashScale * hashChar;
         this._hashScale *= 31;
         this._hashPower++;
      }
   }

   static final boolean isDigit(char c) {
      return c >= '0' && c <= '9';
   }

   static final boolean isAlpha(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
   }

   static final boolean isWhitespace(char c) {
      return c == ' ' || c == '-' || c == '.' || c == 160 || c == 8203;
   }

   private final void logCandidate(int index, int hashVal, int digitCount) {
      if (this._logger != null) {
         if (this._appending) {
            this._logger.logCandidateEndIndex(index, hashVal, digitCount);
            return;
         }

         this._logger.logCandidateBeginIndex(index, hashVal, digitCount, this._hashPower);
      }
   }

   static final int access$408(PhoneNumberPatternAnalyzer x0) {
      return x0._digitCount++;
   }
}
