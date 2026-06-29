package net.rim.tid.im.conv.europe.repository;

class WordLearningSimplePrefixTable$SimplePrefixTableWord {
   int start;
   int end;
   char caseCorrection;
   char freq;
   private final WordLearningSimplePrefixTable this$0;

   WordLearningSimplePrefixTable$SimplePrefixTableWord(WordLearningSimplePrefixTable _1) {
      this.this$0 = _1;
   }

   int codedCharAt(int index) {
      int tableIndex = this.start + index;
      return this.this$0._wordsDefTable[tableIndex] & 127;
   }

   int length() {
      return this.end - this.start;
   }

   @Override
   public String toString() {
      LearningGlobalAlphabet alphabet = this.this$0._reader.getAlphabet();
      StringBuffer buf = new StringBuffer();

      for (int i = 0; i < this.length(); i++) {
         int coded = this.codedCharAt(i);
         buf.append(alphabet.charAt(coded));
      }

      return buf.toString();
   }
}
