package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.RegularExpression$SimpleCharacterIterator;

class SpellCheckWordMatch$CharactersIterator implements RegularExpression$SimpleCharacterIterator {
   int index;
   int charsIndex;
   private final SpellCheckWordMatch this$0;

   SpellCheckWordMatch$CharactersIterator(SpellCheckWordMatch _1, int index) {
      this.this$0 = _1;
      this.setIndex(index);
   }

   void setIndex(int index) {
      this.index = index;
      this.charsIndex = 0;
   }

   @Override
   public boolean hasNext() {
      return this.charsIndex < this.this$0.chars.length && this.this$0.chars[this.charsIndex][this.this$0.starts[this.charsIndex] + this.index] != false;
   }

   @Override
   public char next() {
      char ch = (char)this.this$0.chars[this.charsIndex][this.this$0.starts[this.charsIndex] + this.index];
      this.charsIndex++;
      return ch;
   }

   @Override
   public void close() {
      this.this$0.iterators.push(this);
   }
}
