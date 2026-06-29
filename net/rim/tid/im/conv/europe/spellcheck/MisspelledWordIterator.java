package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.spellcheck.TokenBounds;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.BreakIterator;
import net.rim.tid.util.Utils;

public final class MisspelledWordIterator {
   private BreakIterator _breakIterator;
   private StringBufferGap _text;
   private SpellCheckVariantsCreator _creator;
   private Vector _textFilters = new Vector();
   private boolean _ignoreUpperCaseWords = true;
   private boolean _ignoreWordsWithDigits = true;
   private int _ignoreWordsWithSymbolsFilterId = -1;
   private int _emailFilterId = -1;
   private int _urlFilterId = -1;
   private int _fileNameFilterId = -1;
   private int _minimalCheckLength = 2;
   private boolean _isCaseSensitive = true;
   private StringBuffer _tempBuffer = new StringBuffer();
   private Locale _locale;
   private boolean _checkForAmbiguousWords;
   private Word _word = new Word();
   private static IntHashtable _ignoredWords = new IntHashtable();
   private static IntHashtable _changeAllPairs = new IntHashtable();
   private static boolean NEW_LOOKUP = true;

   public MisspelledWordIterator(SpellCheckVariantsCreator aCreator) {
      this._creator = aCreator;
      this.enableEmailFilter(true);
      this.enableUrlFilter(true);
      this.setFilterFileName(true);
      this.setIgnoreWordsWithSymbols(true);
      this._breakIterator = BreakIterator.getInstance(1);
   }

   public final void init(AttributedString aText, int aStart) {
      this.init(aText.getText(), aStart);
   }

   public final void init(StringBufferGap aText, int aStart) {
      this._text = aText;
      if (this._breakIterator != null) {
         this._breakIterator.setText(this._text);
      }

      for (int i = 0; i < this._textFilters.size(); i++) {
         ITextFilter f = (ITextFilter)this._textFilters.elementAt(i);
         f.reset();
      }
   }

   public final boolean setLocale(Locale aLocale) {
      this._locale = aLocale;
      return (this._breakIterator = BreakIterator.getInstance(1, aLocale)) != null;
   }

   public final boolean findNextWord(TokenBounds aBounds) {
      int previousStart = -1;
      int previousEnd = -1;
      byte type = 0;
      int start = aBounds.end;
      int end = start;
      int last_word_start = aBounds.start;
      int last_word_end = aBounds.end;
      int textLen = this._text.length();

      label53:
      while (start != textLen) {
         end = this._breakIterator.following(start);
         if (end == Integer.MAX_VALUE) {
            end = this._text.length();
         }

         if (NEW_LOOKUP) {
            type = this.newNeedsCorrection(this._text, start, end);
            switch (type) {
               case 1:
                  int filtered = this.filter(start, textLen);
                  if (start != filtered) {
                     last_word_start = last_word_end;
                     type = 0;
                     start = filtered;
                     if (start >= textLen) {
                        break label53;
                     }
                     continue;
                  }
               case 3:
                  break label53;
            }
         } else if (this.needsCorrection(this._text, start, end)) {
            int filtered = this.filter(start, textLen);
            if (start != filtered) {
               last_word_start = last_word_end;
               start = filtered;
               if (start >= textLen) {
                  break;
               }
               continue;
            }

            type = 1;
            break;
         }

         if (this.wordIsRepeated(last_word_start, last_word_end, start, end)) {
            type = 2;
            previousStart = last_word_start;
            previousEnd = last_word_end;
            break;
         }

         char ch = this._text.charAt(start);
         if (CharacterUtilities.isLetter(ch)) {
            last_word_start = start;
            last_word_end = end;
         } else if (!CharacterUtilities.isSpaceChar(ch)) {
            last_word_start = last_word_end;
         }

         start = end;
      }

      aBounds.type = type;
      if (type != 0) {
         aBounds.previousStart = previousStart;
         aBounds.previousEnd = previousEnd;
         aBounds.start = start;
         aBounds.end = end;
         return true;
      } else {
         return false;
      }
   }

   public final boolean needsCorrection(StringBufferGap aWord, int aFrom, int aTo) {
      int len = aTo - aFrom;
      if (len < this._minimalCheckLength) {
         return false;
      }

      boolean is_upper_case = true;
      boolean is_lower_case = true;
      boolean has_letters = false;
      char ch = aWord.charAt(aFrom);
      if (!CharacterUtilities.isLetter(ch) && !CharacterUtilities.isDigit(ch)) {
         return false;
      }

      for (int i = aFrom; i < aTo; i++) {
         ch = aWord.charAt(i);
         if (this._ignoreWordsWithDigits) {
            if (CharacterUtilities.isDigit(ch)) {
               return false;
            }
         } else {
            has_letters = has_letters || CharacterUtilities.isLetter(ch);
         }

         boolean current_is_lower = CharacterUtilities.isLowerCase(ch);
         is_upper_case = is_upper_case && !current_is_lower;
         is_lower_case = is_lower_case && current_is_lower;
      }

      if (!this._ignoreWordsWithDigits && !has_letters) {
         return false;
      }

      if (is_upper_case && this._ignoreUpperCaseWords) {
         return false;
      }

      int key = this.hashCode(aWord, aFrom, len, !is_lower_case);
      String ignoredWord;
      return (ignoredWord = (String)_ignoredWords.get(key)) != null
            && (!this._isCaseSensitive || is_upper_case || this.wordsEqual(ignoredWord, aWord, aFrom, len))
         ? false
         : this._creator.wordExists(aWord, aFrom, aTo, true) == 0;
   }

   public final byte newNeedsCorrection(AbstractString str, int from, int to) {
      int len = to - from;
      if (len < this._minimalCheckLength) {
         return 0;
      }

      this._word.init(str, from, to, this._locale);
      if (this._ignoreWordsWithDigits && this._word.containsNum) {
         return 0;
      }

      if (!this._word.containsAlpha) {
         return 0;
      }

      if (!this._word.containsLower && this._ignoreUpperCaseWords) {
         return 0;
      }

      String ignoredWord = (String)_ignoredWords.get(this.hashCode(this._word));
      if (ignoredWord != null) {
         if (this._isCaseSensitive) {
            if (this._word.equals(ignoredWord)) {
               return 0;
            }
         } else if (this._word.equalsIgnoreCase(ignoredWord)) {
            return 0;
         }
      }

      if (this._checkForAmbiguousWords) {
         if (this._creator.exists(this._word)) {
            switch (this._creator.existsAmbiguous(this._word)) {
               case -1:
                  return 3;
               case 0:
               default:
                  return 1;
               case 1:
                  return 0;
            }
         } else {
            return 1;
         }
      } else {
         return (byte)(this._creator.exists(this._word) ? 0 : 1);
      }
   }

   private final boolean wordIsRepeated(int aLastStart, int aLastEnd, int aStart, int aEnd) {
      int len = aLastEnd - aLastStart;
      if (len != 0 && len == aEnd - aStart) {
         for (int i = 0; i < len; i++) {
            if (Utils.toLowerCase(this._text.charAt(aLastStart + i)) != Utils.toLowerCase(this._text.charAt(aStart + i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public final void ignoreAll(StringBuffer aMisspelledWord) {
      int key = this.hashCode(aMisspelledWord, true);
      _ignoredWords.put(key, aMisspelledWord.toString());
   }

   public final String isInChangeAll(StringBufferGap aWord, int aFrom, int aTo) {
      int key = this.hashCode(aWord, aFrom, aTo - aFrom, true);
      MisspelledWordIterator$ChangeAllPair pair = (MisspelledWordIterator$ChangeAllPair)_changeAllPairs.get(key);
      if (pair == null) {
         return null;
      }

      int len = aTo - aFrom;
      byte caseType = CaseCorrector.classifyCase(aWord, aFrom, len);
      if (this._isCaseSensitive && caseType != 1 && !this.wordsEqual(pair._key, aWord, aFrom, len)) {
         return null;
      }

      if (caseType == 0 || caseType == 4) {
         return pair._replacement;
      }

      if (caseType != 1) {
         this._tempBuffer.setLength(0);
         this._tempBuffer.append(pair._replacement);
         this._tempBuffer.setCharAt(0, Utils.toUpperCase(this._tempBuffer.charAt(0)));
         return this._tempBuffer.toString();
      }

      this._tempBuffer.setLength(0);

      for (int i = 0; i < pair._replacement.length(); i++) {
         this._tempBuffer.append(Utils.toUpperCase(pair._replacement.charAt(i)));
      }

      return this._tempBuffer.toString();
   }

   public final void changeAll(StringBuffer aWord, StringBuffer aReplacement) {
      int key = this.hashCode(aWord, true);
      _changeAllPairs.put(key, new MisspelledWordIterator$ChangeAllPair(aWord.toString(), aReplacement.toString()));
   }

   private final int hashCode(StringBuffer aWord, boolean aConvertToLc) {
      int h = 0;
      int length = aWord.length();

      for (int i = 0; i < length; i++) {
         char ch = aWord.charAt(i);
         if (aConvertToLc) {
            ch = Utils.toLowerCase(ch);
         }

         h = 63 * h + ch;
      }

      return h;
   }

   private final int hashCode(Word word) {
      int h = 0;
      word = word.getLower();
      int length = word.length();

      for (int i = 0; i < length; i++) {
         char ch = word.charAt(i);
         h = 63 * h + ch;
      }

      return h;
   }

   private final int hashCode(StringBufferGap aWord, int aOffset, int aLength, boolean aConvertToLc) {
      int h = 0;

      for (int i = 0; i < aLength; i++) {
         char ch = aWord.charAt(aOffset++);
         if (aConvertToLc) {
            ch = Utils.toLowerCase(ch);
         }

         h = 63 * h + ch;
      }

      return h;
   }

   public final void reset() {
      _ignoredWords.clear();
      _changeAllPairs.clear();
   }

   public final int addFilter(ITextFilter aFilter) {
      this._textFilters.addElement(aFilter);
      return aFilter.hashCode();
   }

   public final void removeFilter(int aFilterCode) {
      for (int i = 0; i < this._textFilters.size(); i++) {
         if (this._textFilters.elementAt(i).hashCode() == aFilterCode) {
            this._textFilters.removeElementAt(i);
            return;
         }
      }
   }

   public final void setIgnoreUpperCaseWords(boolean aVal) {
      this._ignoreUpperCaseWords = aVal;
   }

   public final boolean getIgnoreUpperCaseWords() {
      return this._ignoreUpperCaseWords;
   }

   public final void setIgnoreWordsWithDigits(boolean aVal) {
      this._ignoreWordsWithDigits = aVal;
   }

   public final boolean getIgnoreWordsWithDigits() {
      return this._ignoreWordsWithDigits;
   }

   public final void setCheckForAmbiguousWords(boolean checkForAmbiguousWords) {
      this._checkForAmbiguousWords = checkForAmbiguousWords;
   }

   public final boolean getCheckForAmbiguousWords() {
      return this._checkForAmbiguousWords;
   }

   public final void setCaseSensitive(boolean aVal) {
      this._isCaseSensitive = aVal;
   }

   public final void setIgnoreWordsWithSymbols(boolean aVal) {
      if (this._ignoreWordsWithSymbolsFilterId != -1 != aVal) {
         if (aVal) {
            this._ignoreWordsWithSymbolsFilterId = this.addFilter(new SymbolFilter());
            return;
         }

         this.removeFilter(this._ignoreWordsWithSymbolsFilterId);
         this._ignoreWordsWithSymbolsFilterId = -1;
      }
   }

   public final boolean getIgnoreWordsWithSymbols() {
      return this._ignoreWordsWithSymbolsFilterId != -1;
   }

   public final void enableEmailFilter(boolean aVal) {
      if (this._emailFilterId != -1 != aVal) {
         if (aVal) {
            this._emailFilterId = this.addFilter(new EmailFilter());
            return;
         }

         this.removeFilter(this._emailFilterId);
         this._emailFilterId = -1;
      }
   }

   public final void enableUrlFilter(boolean aVal) {
      if (this._urlFilterId != -1 != aVal) {
         if (aVal) {
            this._urlFilterId = this.addFilter(new UrlFilter());
            return;
         }

         this.removeFilter(this._urlFilterId);
         this._urlFilterId = -1;
      }
   }

   public final void setFilterFileName(boolean enable) {
      if (this._fileNameFilterId != -1 != enable) {
         if (enable) {
            this._fileNameFilterId = this.addFilter(new FileNameFilter());
            return;
         }

         this.removeFilter(this._fileNameFilterId);
         this._fileNameFilterId = -1;
      }
   }

   public final void setMinimalCheckLength(int aLength) {
      aLength = Math.max(aLength, 2);
      this._minimalCheckLength = aLength;
   }

   public final int getMinimalCheckLength() {
      return this._minimalCheckLength;
   }

   private final int filter(int aAnchor, int aEnd) {
      int offset = 0;

      for (int i = 0; i < this._textFilters.size(); i++) {
         ITextFilter f = (ITextFilter)this._textFilters.elementAt(i);
         offset = Math.max(offset, f.filter(this._text, aAnchor, aEnd, false));
      }

      return aAnchor + offset;
   }

   private final boolean wordsEqual(String aWord1, StringBufferGap aWord2, int aOffset2, int aLength2) {
      if (aWord1.length() != aLength2) {
         return false;
      }

      for (int i = 0; i < aLength2; i++) {
         char ch = aWord2.charAt(aOffset2++);
         if (i == 0) {
            ch = Utils.toLowerCase(ch);
         }

         if (aWord1.charAt(i) != ch) {
            return false;
         }
      }

      return true;
   }
}
