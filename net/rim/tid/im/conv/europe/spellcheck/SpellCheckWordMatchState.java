package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.RegularExpressionState;

public class SpellCheckWordMatchState implements RegularExpressionState {
   int index;
   private SpellCheckWordMatch expr;

   public void set(SpellCheckWordMatch expr) {
      this.expr = expr;
      this.index = 0;
   }

   @Override
   public Object newMark() {
      return new Object(this.index);
   }

   @Override
   public void rollback(Object mark) {
      this.index = mark;
   }

   @Override
   public long mark() {
      return this.index;
   }

   @Override
   public void rollback(long mark) {
      this.index = (int)mark;
   }

   @Override
   public boolean isFinal() {
      return this.index == this.expr.wordLen;
   }
}
