package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Stack;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Arrays;
import net.rim.tid.im.conv.europe.AccentGrouping;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.europe.repository.KeypadLayout;
import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;
import net.rim.tid.im.conv.europe.repository.RegularExpression;
import net.rim.tid.im.conv.europe.repository.RegularExpression$SimpleCharacterIterator;
import net.rim.tid.im.conv.europe.repository.RegularExpressionState;

public class EditDistance1WordMatch implements RegularExpression {
   protected byte[] wordMap;
   char[][] lower;
   boolean supportsSharedCharacters;
   int len;
   int maxEditDistance;
   private Stack iterators = (Stack)(new Object());
   private Stack strIterators = (Stack)(new Object());
   private Stack alphaIterators = (Stack)(new Object());

   public void setWord(Word word, Locale locale, AccentGrouping accentGrp) {
      this.len = word.length();
      if (this.len * 256 >= this.wordMap.length) {
         this.wordMap = new byte[256 * this.len];

         for (int i = 0; i < this.lower.length; i++) {
            this.lower[i] = new char[this.len];
         }
      }

      Arrays.fill(this.wordMap, (byte)0);
      int mapIndex = 0;
      Word wlower = word.getLower();

      for (int i = 0; i < this.len; i++) {
         char lc = wlower.charAt(i);
         char unaccentLc = AccentGrouping.lowerCaseToAccentless(lc, locale);
         this.lower[0][mapIndex] = unaccentLc;
         if (this.setAcceptableChars(mapIndex, unaccentLc, locale, accentGrp)) {
            if (this.supportsSharedCharacters) {
               unaccentLc = KeypadLayout.getSharedCharacter(unaccentLc);
               if (unaccentLc != 0) {
                  this.setAcceptableChars(mapIndex, unaccentLc, locale, accentGrp);
               }

               this.lower[1][mapIndex] = unaccentLc;
            }

            mapIndex++;
         }
      }

      this.len = mapIndex;
   }

   protected void setAcceptable(int position, char ch) {
      this.wordMap[position * 256 + (ch & 255)] = 1;
   }

   protected boolean setAcceptableChars(int position, char unaccentLc, Locale locale, AccentGrouping accentGrp) {
      char unaccentUc = CaseCorrector.toUpperCase(unaccentLc, locale);
      this.setAcceptable(position, unaccentLc);
      this.setAcceptable(position, unaccentUc);
      String accents = accentGrp.getAccents(unaccentLc);
      if (accents != null) {
         for (int j = 0; j < accents.length(); j++) {
            char lc = accents.charAt(j);
            this.setAcceptable(position, lc);
            char uc = CaseCorrector.toUpperCase(lc, locale);
            this.setAcceptable(position, uc);
         }
      }

      return true;
   }

   protected boolean canAccept(int index, char ch) {
      return this.wordMap[index * 256 + (ch & 255)] != 0;
   }

   @Override
   public RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState state, LearningGlobalAlphabet charsStr) {
      EditDistance1WordMatchState mstate = (EditDistance1WordMatchState)state;
      if (mstate.t1PathIndex != -1) {
         EditDistance1WordMatch$ReusableAlphabetCharacterIterator iter;
         if (this.alphaIterators.empty()) {
            iter = new EditDistance1WordMatch$ReusableAlphabetCharacterIterator(this, charsStr);
         } else {
            iter = (EditDistance1WordMatch$ReusableAlphabetCharacterIterator)this.alphaIterators.pop();
            iter.setAlphabet(charsStr);
         }

         return iter;
      } else {
         return this.getAcceptableChars(state, (String)((Object)null));
      }
   }

   @Override
   public RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState state, String charsStr) {
      EditDistance1WordMatchState mstate = (EditDistance1WordMatchState)state;
      if (mstate.t1PathIndex != -1) {
         EditDistance1WordMatch$ReusableStringCharacterIterator iter;
         if (this.strIterators.empty()) {
            iter = new EditDistance1WordMatch$ReusableStringCharacterIterator(this, charsStr);
         } else {
            iter = (EditDistance1WordMatch$ReusableStringCharacterIterator)this.strIterators.pop();
            iter.setString(charsStr);
         }

         return iter;
      } else {
         EditDistance1WordMatch$PositionCharacterIterator iter;
         if (this.iterators.empty()) {
            iter = new EditDistance1WordMatch$PositionCharacterIterator(this);
         } else {
            iter = (EditDistance1WordMatch$PositionCharacterIterator)this.iterators.pop();
         }

         iter.reset();
         if (mstate.insertPathIndex != -1 && mstate.insertPathIndex < this.len) {
            iter.addPosition(mstate.insertPathIndex);
         }

         if (mstate.replacePathIndex != -1 && mstate.replacePathIndex < this.len) {
            iter.addPosition(mstate.replacePathIndex);
         }

         if (mstate.deletePathIndex != -1 && mstate.deletePathIndex < this.len) {
            iter.addPosition(mstate.deletePathIndex);
         }

         if (mstate.swap1PathIndex != -1 && mstate.swap1PathIndex < this.len) {
            iter.addPosition(mstate.swap1PathIndex);
         } else if (mstate.swap2PathIndex != -1 && mstate.swap2PathIndex < this.len) {
            iter.addPosition(mstate.swap2PathIndex);
         }

         return iter;
      }
   }

   @Override
   public boolean acceptsLength(RegularExpressionState state, int acceptLen, boolean isLowerBound, boolean isUpperBound) {
      EditDistance1WordMatchState mstate = (EditDistance1WordMatchState)state;
      if (isLowerBound == isUpperBound) {
         if (acceptLen == this.len) {
            return mstate.replacePathIndex != -1 || mstate.swap1PathIndex != -1 || mstate.swap2PathIndex != -1 || mstate.t1PathIndex != -1;
         } else if (acceptLen == this.len - 1) {
            return mstate.deletePathIndex != -1 || mstate.t1PathIndex != -1;
         } else {
            return acceptLen == this.len + 1 ? mstate.insertPathIndex != -1 || mstate.t1PathIndex != -1 : false;
         }
      } else if (isLowerBound) {
         if (acceptLen > this.len + 1) {
            return false;
         } else if (acceptLen < this.len) {
            return true;
         } else {
            return acceptLen == this.len + 1
               ? mstate.insertPathIndex != -1 || mstate.t1PathIndex != -1
               : mstate.replacePathIndex != -1
                  || mstate.insertPathIndex != -1
                  || mstate.swap1PathIndex != -1
                  || mstate.swap2PathIndex != -1
                  || mstate.t1PathIndex != -1;
         }
      } else if (acceptLen < this.len - 1) {
         return false;
      } else if (acceptLen > this.len) {
         return true;
      } else {
         return acceptLen == this.len - 1
            ? mstate.deletePathIndex != -1 || mstate.t1PathIndex != -1
            : mstate.replacePathIndex != -1
               || mstate.deletePathIndex != -1
               || mstate.swap1PathIndex != -1
               || mstate.swap2PathIndex != -1
               || mstate.t1PathIndex != -1;
      }
   }

   @Override
   public boolean accept(RegularExpressionState state, char[] chs) {
      Object mark = state.newMark();
      int slen = chs.length;

      for (int i = 0; i < slen; i++) {
         if (!this.accept(state, chs[i])) {
            state.rollback(mark);
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean accept(RegularExpressionState state, String str) {
      Object mark = state.newMark();
      int slen = str.length();

      for (int i = 0; i < slen; i++) {
         if (!this.accept(state, str.charAt(i))) {
            state.rollback(mark);
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean accept(RegularExpressionState state, char ch) {
      EditDistance1WordMatchState mstate = (EditDistance1WordMatchState)state;
      if (mstate.t1PathIndex == -1) {
         boolean acceptable = false;
         int swap1PathIndex = mstate.swap1PathIndex;
         int swap2PathIndex = mstate.swap2PathIndex;
         int insertPathIndex = mstate.insertPathIndex;
         int deletePathIndex = mstate.deletePathIndex;
         int replacePathIndex = mstate.replacePathIndex;
         if (swap2PathIndex != -1) {
            if (swap2PathIndex >= this.len) {
               swap2PathIndex = -1;
            } else if (this.canAccept(swap2PathIndex, ch)) {
               mstate.lastAccepted = swap2PathIndex;
               acceptable = true;
               swap2PathIndex++;
            } else {
               swap2PathIndex = -1;
            }
         }

         if (swap1PathIndex != -1) {
            if (this.canAccept(swap1PathIndex, ch)) {
               mstate.lastAccepted = swap1PathIndex;
               swap2PathIndex = swap1PathIndex + 2;
               acceptable = true;
            }

            swap1PathIndex = -1;
         }

         if (deletePathIndex != -1) {
            if (deletePathIndex >= this.len) {
               deletePathIndex = -1;
            } else if (this.canAccept(deletePathIndex, ch)) {
               mstate.lastAccepted = deletePathIndex;
               acceptable = true;
               deletePathIndex++;
            } else {
               deletePathIndex = -1;
            }
         }

         if (insertPathIndex != -1) {
            if (insertPathIndex >= this.len) {
               insertPathIndex = -1;
            } else if (this.canAccept(insertPathIndex, ch)) {
               mstate.lastAccepted = insertPathIndex;
               acceptable = true;
               insertPathIndex++;
            } else {
               insertPathIndex = -1;
            }
         }

         if (replacePathIndex != -1) {
            if (replacePathIndex >= this.len) {
               replacePathIndex = -1;
            } else if (this.canAccept(replacePathIndex, ch)) {
               mstate.lastAccepted = replacePathIndex;
               acceptable = true;
               replacePathIndex++;
            } else {
               replacePathIndex = -1;
            }
         }

         if (acceptable) {
            mstate.swap1PathIndex = swap1PathIndex;
            mstate.swap2PathIndex = swap2PathIndex;
            mstate.insertPathIndex = insertPathIndex;
            mstate.deletePathIndex = deletePathIndex;
            mstate.replacePathIndex = replacePathIndex;
         }

         return acceptable;
      } else if (mstate.t1PathIndex >= this.len) {
         if (this.maxEditDistance == 0) {
            return false;
         }

         mstate.deletePathIndex = mstate.t1PathIndex;
         mstate.t1PathIndex = -1;
         return true;
      } else {
         int index = mstate.t1PathIndex;
         if (this.canAccept(index, ch)) {
            mstate.lastAccepted = index;
            mstate.t1PathIndex++;
            return true;
         }

         if (this.maxEditDistance == 0) {
            return false;
         }

         mstate.insertPathIndex = mstate.t1PathIndex;
         mstate.replacePathIndex = mstate.t1PathIndex + 1;
         if (index + 1 < this.len && this.canAccept(index + 1, ch)) {
            mstate.lastAccepted = index + 1;
            if (mstate.t1PathIndex + 2 <= this.len) {
               mstate.deletePathIndex = mstate.t1PathIndex + 2;
            }

            mstate.swap1PathIndex = mstate.t1PathIndex;
         }

         mstate.t1PathIndex = -1;
         return true;
      }
   }

   public EditDistance1WordMatch(int maxEditDistance) {
      this.maxEditDistance = maxEditDistance;
      this.supportsSharedCharacters = KeypadLayout.supportsSharedCharacters();
      if (this.supportsSharedCharacters) {
         this.lower = new char[2][30];
      } else {
         this.lower = new char[1][30];
      }

      this.wordMap = new byte[7680];
   }
}
