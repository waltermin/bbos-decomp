package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;
import net.rim.tid.im.conv.europe.repository.RegularExpression$AlphabetCharacterIterator;

class EditDistance1WordMatch$ReusableAlphabetCharacterIterator extends RegularExpression$AlphabetCharacterIterator {
   private final EditDistance1WordMatch this$0;

   EditDistance1WordMatch$ReusableAlphabetCharacterIterator(EditDistance1WordMatch _1, LearningGlobalAlphabet alpha) {
      super(alpha);
      this.this$0 = _1;
   }

   @Override
   protected void setAlphabet(LearningGlobalAlphabet alpha) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void close() {
      this.this$0.alphaIterators.push(this);
   }
}
