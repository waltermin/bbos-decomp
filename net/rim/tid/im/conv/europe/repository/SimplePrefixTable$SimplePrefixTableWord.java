package net.rim.tid.im.conv.europe.repository;

class SimplePrefixTable$SimplePrefixTableWord {
   int start;
   int end;
   char caseCorrection;
   private final SimplePrefixTable this$0;

   SimplePrefixTable$SimplePrefixTableWord(SimplePrefixTable _1) {
      this.this$0 = _1;
   }

   int codedCharAt(int index) {
      int tableIndex = this.start + index;
      return this.this$0.iWordsDefTable[tableIndex] & 127;
   }

   int length() {
      return this.end - this.start;
   }

   @Override
   public String toString() {
      int substShift = this.this$0.iReader.getSubstShift();
      String alphabet = this.this$0.iReader.getAlphabet();
      Reader$SubstitutionTable substTable = this.this$0.iReader.getSubstitutionTable();
      StringBuffer buf = (StringBuffer)(new Object());

      for (int i = 0; i < this.length(); i++) {
         int coded = this.codedCharAt(i);
         if (coded < substShift) {
            buf.append(alphabet.charAt(coded));
         } else {
            buf.append(substTable.getSubstitution(coded, substShift));
         }
      }

      return buf.toString();
   }
}
