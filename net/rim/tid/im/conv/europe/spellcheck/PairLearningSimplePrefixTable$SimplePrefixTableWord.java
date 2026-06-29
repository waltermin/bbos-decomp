package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;

class PairLearningSimplePrefixTable$SimplePrefixTableWord {
   int start;
   int end;
   int pairedStart;
   int pairedEnd;
   private final PairLearningSimplePrefixTable this$0;

   PairLearningSimplePrefixTable$SimplePrefixTableWord(PairLearningSimplePrefixTable _1) {
      this.this$0 = _1;
   }

   int codedCharAt(int index) {
      int tableIndex = this.start + index;
      return PairLearningSimplePrefixTable.access$000(this.this$0)[tableIndex] & 127;
   }

   int length() {
      return this.end - this.start;
   }

   @Override
   public String toString() {
      LearningGlobalAlphabet alphabet = PairLearningSimplePrefixTable.access$100(this.this$0).getAlphabet();
      StringBuffer buf = (StringBuffer)(new Object());

      for (int i = 0; i < this.length(); i++) {
         int coded = this.codedCharAt(i);
         buf.append(alphabet.charAt(coded));
      }

      return buf.toString();
   }
}
