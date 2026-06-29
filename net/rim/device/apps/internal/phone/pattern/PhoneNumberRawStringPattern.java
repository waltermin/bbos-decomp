package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class PhoneNumberRawStringPattern extends StringPattern implements PhoneNumberPatternAnalyzer$Logger {
   private WeakReference _scanbufferWR;
   private PhoneNumberPatternAnalyzer _analyzer;
   private WeakReference _stringWR;
   private int _smartDialingCountryCode;
   private int[] _smartDialingNNLRange;
   private int _savedCount;
   private int _savedMax = 16;
   private int[] _saved = new int[80];
   private int _beginIndex;
   private int _skipCount;
   private boolean _allowSpaces;
   private int _endIndex;
   private boolean _isSearchOnCursor;
   private static final int MINIMUM_PATTERN_DIGIT_COUNT;
   private static final int MIN_CURSOR_DIGITCOUNT;
   private static final int MAX_CURSOR_DIGITCOUNT;
   private static final int SCAN_BUFFER_SIZE;
   private static final int SAVED_INCREMENT;
   private static final int SAVED_CHUNK_SIZE;
   private static final int MAX_EXT_DIGITS;
   private static final String EXTENSION_KEY;

   public PhoneNumberRawStringPattern() {
      this._scanbufferWR = (WeakReference)(new Object(null));
      this._stringWR = (WeakReference)(new Object(null));
      this._analyzer = new PhoneNumberPatternAnalyzer(this);
   }

   @Override
   public final long getPatternTypeIdentifier() {
      return 3797587162219887872L;
   }

   public static final PhoneNumberRawStringPattern getRegisteredStringPattern() {
      StringPatternContainer patterns = StringPatternRepository$Internal.getStringPatterns();

      for (int idx = patterns.size() - 1; idx >= 0; idx--) {
         if (patterns.getAt(idx) instanceof PhoneNumberRawStringPattern) {
            return (PhoneNumberRawStringPattern)patterns.getAt(idx);
         }
      }

      return null;
   }

   @Override
   public final synchronized boolean findMatch(AbstractString str, int index, int maxIndex, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      this._smartDialingCountryCode = SmartDialingOptions.getOptions().getCountryCode();
      this._smartDialingNNLRange = WorldPhoneInfo.getNationalPhoneNumberLengthRange(this._smartDialingCountryCode);
      int minIndex = index;
      char[] scanbuffer = WeakReferenceUtilities.getCharArray(this._scanbufferWR, 32);

      while (index < maxIndex) {
         int newIndex = this.getNextIndex(scanbuffer, str, index, maxIndex);
         if (newIndex < index || newIndex >= maxIndex) {
            return false;
         }

         if (this.delimitPhoneNumber(scanbuffer, str, newIndex, minIndex, maxIndex)) {
            match.id = 3797587162219887872L;
            match.beginIndex = this._beginIndex;
            match.endIndex = this._endIndex;
            match.endIndex = match.endIndex + getExtensionLength(str, match.endIndex, maxIndex);
            match.prefixLength = 0;
            return true;
         }

         index = newIndex + 1;
      }

      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized boolean findMatch(AbstractString str, int minIndex, int curIndex, int maxIndex, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      char[] scanbuffer = WeakReferenceUtilities.getCharArray(this._scanbufferWR, 32);
      boolean found = false;
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         this._isSearchOnCursor = true;
         found = this.delimitPhoneNumber(scanbuffer, str, curIndex, minIndex, maxIndex);
         var10 = false;
      } finally {
         if (var10) {
            this._isSearchOnCursor = false;
         }
      }

      this._isSearchOnCursor = false;
      if (found) {
         match.id = 3797587162219887872L;
         match.beginIndex = this._beginIndex;
         match.endIndex = this._endIndex;
         match.endIndex = match.endIndex + getExtensionLength(str, match.endIndex, maxIndex);
         match.prefixLength = 0;
      }

      return found;
   }

   private final boolean delimitPhoneNumber(char[] scanbuffer, AbstractString str, int foundIndex, int minIndex, int maxIndex) {
      if (foundIndex - minIndex + 1 > scanbuffer.length) {
         minIndex = foundIndex - scanbuffer.length + 1;
      }

      str.getChars(minIndex, foundIndex + 1, scanbuffer, 0);
      this._analyzer.reset();
      this._stringWR.set(str);
      this._savedCount = 0;
      this._beginIndex = 0;
      this._endIndex = 0;
      boolean foundFirstSpace = false;

      for (int index = foundIndex; index >= minIndex; index--) {
         char ch = CharacterUtilities.foldFullWidth(scanbuffer[index - minIndex]);
         if (ch == ' ' && !foundFirstSpace) {
            this.addCandidate(index + 1, 0, this._analyzer.getCurrentHashValue(), this._analyzer.getCurrentDigitCount(), false);
            foundFirstSpace = true;
         }

         this._analyzer.prepend(ch, index);
         if (this._analyzer.isDone()) {
            break;
         }
      }

      if (!this._analyzer.isDone()) {
         this._analyzer.prepend('\u0000', minIndex);
         if (!foundFirstSpace) {
            this.addCandidate(minIndex, 0, this._analyzer.getCurrentHashValue(), this._analyzer.getCurrentDigitCount(), false);
         }
      }

      if (this._savedCount > 0) {
         if (maxIndex - foundIndex > scanbuffer.length) {
            maxIndex = foundIndex + scanbuffer.length;
         }

         str.getChars(foundIndex, maxIndex, scanbuffer, 0);

         for (int candidateIdx = this._savedCount - 1; candidateIdx >= 0; candidateIdx--) {
            this.resetToCandidate(candidateIdx);

            for (int index = foundIndex + 1; index < maxIndex; index++) {
               char ch = CharacterUtilities.foldFullWidth(scanbuffer[index - foundIndex]);
               if (ch == ' ' && !this._allowSpaces) {
                  this._analyzer.append('\u0000', index);
                  break;
               }

               this._analyzer.append(ch, index);
               if (this._analyzer.isDone()) {
                  break;
               }
            }

            if (!this._analyzer.isDone()) {
               this._analyzer.append('\u0000', maxIndex - 1);
            }

            if (this._endIndex > this._beginIndex) {
               break;
            }
         }
      }

      this._stringWR.set(null);
      return this._endIndex > this._beginIndex;
   }

   public static final int hashCode(String str) {
      PhoneNumberPatternAnalyzer analyzer = new PhoneNumberPatternAnalyzer();
      return analyzer.calculateHashValue(str);
   }

   private final int getNextIndex(char[] scanbuffer, AbstractString str, int index, int maxIndex) {
      int digitCount = 0;
      int requiredDigitCount = 6;

      while (index < maxIndex) {
         int count = maxIndex - index;
         if (count > scanbuffer.length) {
            count = scanbuffer.length;
         }

         str.getChars(index, index + count, scanbuffer, 0);

         for (int bufIndex = 0; bufIndex < count; index++) {
            switch (CharacterUtilities.foldFullWidth(scanbuffer[bufIndex])) {
               case ' ':
               case '(':
               case ')':
               case '-':
               case '.':
               case ' ':
               case '\u200b':
                  break;
               case '#':
               case '*':
                  digitCount = 1;
                  break;
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  if (++digitCount >= requiredDigitCount) {
                     return index;
                  }
                  break;
               default:
                  digitCount = 0;
            }

            bufIndex++;
         }
      }

      return index;
   }

   @Override
   public final void logCandidateBeginIndex(int index, int hashVal, int digitCount, int hashPower) {
      this.addCandidate(index, 0, hashVal, digitCount, true);
      AbstractString string = (AbstractString)this._stringWR.get();
      if (string.charAt(index) == '0') {
         int cc = SmartDialingOptions.getOptions().getCountryCode();
         char[] idd = WorldPhoneInfo.getInternationalDialingDigits(cc, cc);
         if (idd != null) {
            int lengthRemain = string.length() - index - 1;
            int len = idd.length;
            if (idd.length <= lengthRemain && stringEquals(string, index, len, idd)) {
               this.addIDDCandidate(string, index, hashVal, digitCount, hashPower, len);
            }
         }
      }
   }

   private static final boolean stringEquals(AbstractString string, int start, int count, char[] buf) {
      for (int pos = 0; pos < count; pos++) {
         if (buf[pos] != string.charAt(start + pos)) {
            return false;
         }
      }

      return true;
   }

   private final void addIDDCandidate(AbstractString string, int index, int hashVal, int digitCount, int hashPower, int skipCount) {
      for (int idx = 0; idx < skipCount; idx++) {
         digitCount--;
         hashPower--;
         hashVal -= 48 * hashScale(hashPower);
      }

      if (PhoneNumberPatternAnalyzer.isWhitespace(string.charAt(index + skipCount))) {
         hashPower--;
         hashVal -= 45 * hashScale(hashPower);

         while (PhoneNumberPatternAnalyzer.isWhitespace(string.charAt(index + ++skipCount))) {
         }
      }

      hashVal += 43 * hashScale(hashPower);
      this.addCandidate(index, skipCount, hashVal, digitCount, true);
   }

   private static final int hashScale(int hashPower) {
      int hashScale = 1;

      while (hashPower > 0) {
         hashScale *= 31;
         hashPower--;
      }

      return hashScale;
   }

   private final void addCandidate(int index, int skipCount, int hashVal, int digitCount, boolean allowSpaces) {
      if (this._savedCount == this._savedMax) {
         Array.resize(this._saved, (this._savedMax + 16) * 5);
         this._savedMax += 16;
      }

      int idx = this._savedCount * 5;
      if (!allowSpaces) {
         System.arraycopy(this._saved, 0, this._saved, 5, this._savedCount * 5);
         idx = 0;
      }

      this._saved[idx++] = index;
      this._saved[idx++] = hashVal;
      this._saved[idx++] = digitCount;
      this._saved[idx++] = skipCount;
      this._saved[idx++] = allowSpaces ? 1 : 0;
      this._savedCount++;
   }

   private final void resetToCandidate(int candidateIndex) {
      int idx = candidateIndex * 5;
      this._beginIndex = this._endIndex = this._saved[idx];
      this._analyzer.reset(this._saved[idx + 1], this._saved[idx + 2]);
      this._skipCount = this._saved[idx + 3];
      this._allowSpaces = this._saved[idx + 4] != 0;
   }

   @Override
   public final void logCandidateEndIndex(int index, int hashVal, int digitCount) {
      AbstractString string = (AbstractString)this._stringWR.get();
      if (this._isSearchOnCursor && digitCount >= 3 && digitCount <= 20) {
         this._endIndex = index + 1;
      } else if (PhoneNumberPatternSet.validatePhoneNumber(hashVal, this._smartDialingCountryCode, string, this._beginIndex + this._skipCount, digitCount) > 0) {
         this._endIndex = index + 1;
      } else {
         if (this.isLocalRunOfDigits(string, index + 1 - this._beginIndex - this._skipCount, digitCount)) {
            this._endIndex = index + 1;
         }
      }
   }

   private final boolean isLocalRunOfDigits(AbstractString string, int charCount, int digitCount) {
      return charCount == digitCount
            && this._smartDialingNNLRange != null
            && this._smartDialingNNLRange[0] <= digitCount
            && digitCount <= this._smartDialingNNLRange[1]
         ? PhoneNumberPatternSet.testLocalPhoneNumber(this._smartDialingCountryCode, string, this._beginIndex + this._skipCount, digitCount)
         : false;
   }

   static final int getExtensionLength(AbstractString str, int beginIndex, int maxIndex) {
      char[] ext = "ext".toCharArray();
      int charCount = 0;
      boolean matchedAllChars = false;
      boolean hasOpenBracket = false;
      int index = beginIndex;

      while (index < maxIndex) {
         char ch = str.charAt(index++);
         char chLower = Character.toLowerCase(ch);
         if (chLower == 'x') {
            if (chLower != ch && index > 1) {
               char prevChar = str.charAt(index - 2);
               if (!StringPattern.isWhitespace(prevChar) && !PhoneNumberStringPattern.isPunctuation(prevChar)) {
                  return 0;
               }
            }

            matchedAllChars = true;
            break;
         }

         if (chLower == ext[0]) {
            charCount = 1;
            break;
         }

         if (ch == '(' && !hasOpenBracket) {
            hasOpenBracket = true;
         } else if (ch != ',' && !isExtWhitespace(ch)) {
            return 0;
         }
      }

      while (!matchedAllChars && index < maxIndex) {
         char ch = str.charAt(index++);
         if (ch != ext[charCount]) {
            return 0;
         }

         if (++charCount == ext.length) {
            matchedAllChars = true;
         }
      }

      int digitCount = 0;

      while (index < maxIndex) {
         char ch = str.charAt(index);
         if (PhoneNumberServices.isDTMFKey(ch)) {
            if (++digitCount > 16) {
               return 0;
            }
         } else if (!isExtWhitespace(ch) || digitCount != 0) {
            if (ch == ')' && hasOpenBracket && digitCount > 1) {
               return index - beginIndex + 1;
            }
            break;
         }

         index++;
      }

      return matchedAllChars && digitCount > 1 ? index - beginIndex : 0;
   }

   private static final boolean isExtWhitespace(char c) {
      return c == ' ' || c == '.' || c == '-' || c == '/';
   }
}
