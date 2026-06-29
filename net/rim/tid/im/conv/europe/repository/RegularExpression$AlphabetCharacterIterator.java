package net.rim.tid.im.conv.europe.repository;

public class RegularExpression$AlphabetCharacterIterator implements RegularExpression$SimpleCharacterIterator {
   LearningGlobalAlphabet alpha;
   int index;

   protected void setAlphabet(LearningGlobalAlphabet alpha) {
      this.alpha = alpha;
      this.index = 0;
   }

   @Override
   public char next() {
      return this.alpha.charAt(this.index++);
   }

   @Override
   public void close() {
   }

   @Override
   public boolean hasNext() {
      return this.index < this.alpha.length();
   }

   public RegularExpression$AlphabetCharacterIterator(LearningGlobalAlphabet alpha) {
      this.setAlphabet(alpha);
   }
}
