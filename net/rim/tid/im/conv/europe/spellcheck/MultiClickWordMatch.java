package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.RegularExpression$SimpleCharacterIterator;
import net.rim.tid.im.conv.europe.repository.RegularExpressionState;

public class MultiClickWordMatch extends EditDistance1WordMatch {
   public MultiClickWordMatch(int maxEditDistance) {
      super(maxEditDistance);
   }

   @Override
   public RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState state, String charsStr) {
      EditDistance1WordMatchState mstate = (EditDistance1WordMatchState)state;
      if (mstate.t1PathIndex != -1) {
         return super.getAcceptableChars(state, charsStr);
      }

      EditDistance1WordMatch$PositionCharacterIterator iter = (EditDistance1WordMatch$PositionCharacterIterator)super.getAcceptableChars(state, charsStr);
      if (mstate.lastAccepted != -1 && mstate.lastAccepted < super.len) {
         iter.addPosition(mstate.lastAccepted);
      }

      return iter;
   }

   @Override
   public boolean accept(RegularExpressionState state, char ch) {
      EditDistance1WordMatchState mstate = (EditDistance1WordMatchState)state;
      return mstate.lastAccepted != -1 && this.canAccept(mstate.lastAccepted, ch) ? true : super.accept(state, ch);
   }

   @Override
   public boolean acceptsLength(RegularExpressionState state, int acceptLen, boolean isLowerBound, boolean isUpperBound) {
      return true;
   }
}
