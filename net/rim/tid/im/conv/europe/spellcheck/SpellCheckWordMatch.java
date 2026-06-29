package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Stack;
import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.europe.repository.KeypadLayout;
import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;
import net.rim.tid.im.conv.europe.repository.RegularExpression;
import net.rim.tid.im.conv.europe.repository.RegularExpression$SimpleCharacterIterator;
import net.rim.tid.im.conv.europe.repository.RegularExpressionState;
import net.rim.vm.Array;

public class SpellCheckWordMatch implements RegularExpression {
   int wordLen;
   private char[][] chars;
   private int[] starts;
   private boolean isCaseSensitive;
   private boolean checkShared;
   private Stack iterators = (Stack)(new Object());

   public SpellCheckWordMatch(boolean isCaseSensitive, boolean checkShared) {
      if (isCaseSensitive && checkShared) {
         throw new Object();
      }

      this.isCaseSensitive = isCaseSensitive;
      this.checkShared = checkShared;
      int sz = isCaseSensitive ? 1 : 2;
      sz *= checkShared ? 2 : 1;
      this.chars = new char[sz][];
      this.starts = new int[sz];
   }

   public void setWord(Word word) {
      this.wordLen = word.length();
      int index = 0;
      if (this.isCaseSensitive) {
         this.chars[index] = word.text;
         this.starts[index] = word.start;
      } else {
         Word lower = word.getLower();
         this.chars[index] = lower.text;
         this.starts[index] = lower.start;
         index++;
         Word upper = word.getUpper();
         this.chars[index] = upper.text;
         this.starts[index] = upper.start;
         if (this.checkShared) {
            char[] currBuff = this.chars[++index];
            if (currBuff == null) {
               currBuff = new char[this.wordLen];
               this.chars[index] = currBuff;
            } else if (currBuff.length < this.wordLen) {
               Array.resize(currBuff, this.wordLen);
            }

            for (int i = 0; i < this.wordLen; i++) {
               char ch = lower.charAt(i);
               currBuff[i] = KeypadLayout.getSharedCharacter(ch);
            }

            this.starts[index] = 0;
            char[] ucurrBuff = this.chars[++index];
            if (ucurrBuff == null) {
               ucurrBuff = new char[this.wordLen];
               this.chars[index] = ucurrBuff;
            } else if (ucurrBuff.length < this.wordLen) {
               Array.resize(ucurrBuff, this.wordLen);
            }

            Locale locale = word.getLocale();

            for (int i = 0; i < this.wordLen; i++) {
               char ch = currBuff[i];
               if (ch == 0) {
                  ucurrBuff[i] = ch;
               } else {
                  ucurrBuff[i] = CaseCorrector.toUpperCase(ch, locale);
               }
            }

            this.starts[index] = 0;
         }
      }
   }

   @Override
   public boolean accept(RegularExpressionState state, char ch) {
      SpellCheckWordMatchState mstate = (SpellCheckWordMatchState)state;
      int index = mstate.index;
      if (index >= 0 && index < this.wordLen) {
         for (int i = 0; i < this.chars.length; i++) {
            if (this.chars[i][index + this.starts[i]] == ch) {
               mstate.index++;
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean accept(RegularExpressionState state, String str) {
      SpellCheckWordMatchState mstate = (SpellCheckWordMatchState)state;
      int index = mstate.index;
      if (index >= 0 && index < this.wordLen) {
         int strlen = str.length();

         label27:
         for (int j = 0; j < strlen; j++) {
            char ch = str.charAt(j);

            for (int i = 0; i < this.chars.length; i++) {
               if (this.chars[i][index + this.starts[i]] == ch) {
                  index++;
                  continue label27;
               }
            }

            return false;
         }

         mstate.index = index;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean accept(RegularExpressionState state, char[] chs) {
      SpellCheckWordMatchState mstate = (SpellCheckWordMatchState)state;
      int index = mstate.index;
      if (index >= 0 && index < this.wordLen) {
         label27:
         for (char ch : chs) {
            for (int i = 0; i < this.chars.length; i++) {
               if (this.chars[i][index + this.starts[i]] == ch) {
                  index++;
                  continue label27;
               }
            }

            return false;
         }

         mstate.index = index;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean acceptsLength(RegularExpressionState state, int len, boolean isLowerBound, boolean isUpperBound) {
      if (isLowerBound == isUpperBound) {
         return len == this.wordLen;
      } else {
         return isLowerBound ? len <= this.wordLen : len >= this.wordLen;
      }
   }

   @Override
   public RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState state, String charsStr) {
      SpellCheckWordMatchState mstate = (SpellCheckWordMatchState)state;
      int index = mstate.index;
      if (index < 0 || index >= this.wordLen) {
         return RegularExpression.EMPTY_ITER;
      }

      if (this.iterators.empty()) {
         return new SpellCheckWordMatch$CharactersIterator(this, index);
      }

      SpellCheckWordMatch$CharactersIterator iter = (SpellCheckWordMatch$CharactersIterator)this.iterators.pop();
      iter.setIndex(index);
      return iter;
   }

   @Override
   public RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState state, LearningGlobalAlphabet charsStr) {
      return this.getAcceptableChars(state, (String)((Object)null));
   }
}
