package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.RegularExpression$StringCharacterIterator;

class EditDistance1WordMatch$ReusableStringCharacterIterator extends RegularExpression$StringCharacterIterator {
   private final EditDistance1WordMatch this$0;

   EditDistance1WordMatch$ReusableStringCharacterIterator(EditDistance1WordMatch _1, String str) {
      super(str);
      this.this$0 = _1;
   }

   @Override
   protected void setString(String str) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void close() {
      this.this$0.strIterators.push(this);
   }
}
