package net.rim.tid.im.conv.europe;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class CaseCorrector {
   private int _inputLength;
   private boolean[] _userCaseSet;
   private char[] _userInput;
   private boolean[] _suffixCaseSet;
   private int _suffixSetLen;
   boolean _caseNeedsCorrection = false;
   boolean _allUpper = true;
   boolean _allLetters = true;
   private Locale _locale;
   private ExtendedCurrentVariant _correctionVariant = (ExtendedCurrentVariant)(new Object());
   private WeakReference _tempCharBuffer = (WeakReference)(new Object(null));
   public static final byte LOWER_CASE;
   public static final byte UPPER_CASE;
   public static final byte CAPITALIZED;
   public static final byte MIXED_CAPITALIZED;
   public static final byte MIXED_CASE;
   private static WeakReference iTempBufferWR = (WeakReference)(new Object(null));

   public CaseCorrector(Locale aLocale, int aLongestWordLength) {
      this._locale = aLocale;
      this._userCaseSet = new boolean[aLongestWordLength];
      this._userInput = new char[aLongestWordLength];
      this._suffixCaseSet = new boolean[aLongestWordLength / 4];
   }

   public int getInputLength() {
      return this._inputLength;
   }

   public void reset() {
      this.resetSuffixCaseSet();
      this._inputLength = 0;
      this._allUpper = true;
      this._caseNeedsCorrection = false;
      this._allLetters = true;
   }

   public boolean backspace() {
      if (this._inputLength > 0) {
         this._inputLength--;
         return this._userCaseSet[this._inputLength];
      } else {
         return false;
      }
   }

   public void keyTyped(char aKeyChar) {
      if (this._inputLength == this._userCaseSet.length) {
         Array.resize(this._userCaseSet, this._inputLength + 5);
         Array.resize(this._userInput, this._inputLength + 5);
      }

      boolean is_upper = CharacterUtilities.isUpperCase(aKeyChar);
      this._userInput[this._inputLength] = aKeyChar;
      this._userCaseSet[this._inputLength++] = is_upper;
      this._caseNeedsCorrection = this._caseNeedsCorrection || is_upper;
      this._allUpper = this._allUpper && is_upper;
      this._allLetters = this._allLetters && (is_upper || CharacterUtilities.isLowerCase(aKeyChar));
   }

   public void keysTyped(StringBuffer aChars, int length) {
      for (int i = 0; i < length; i++) {
         this.keyTyped(aChars.charAt(i));
      }
   }

   public void keysTyped(SLCurrentVariant aWord) {
      int start = aWord._offset;
      int end = start + aWord._length;

      for (int i = start; i < end; i++) {
         this.keyTyped(aWord._variants[i]);
      }
   }

   public final void setSuffixCases(StringBuffer suffix, int start, int len) {
      this._suffixSetLen = len;
      if (this._suffixCaseSet.length < len) {
         Array.resize(this._suffixCaseSet, len);
      }

      for (int i = 0; i < len; i++) {
         char ch = suffix.charAt(start + i);
         boolean is_upper = CharacterUtilities.isUpperCase(ch);
         this._suffixCaseSet[i] = is_upper;
         this._caseNeedsCorrection = this._caseNeedsCorrection || is_upper;
         this._allUpper = this._allUpper && is_upper;
         this._allLetters = this._allLetters && (is_upper || CharacterUtilities.isLowerCase(ch));
      }
   }

   public final void resetSuffixCaseSet() {
      this._suffixSetLen = 0;
   }

   public boolean lastIsUpper() {
      return this._userCaseSet[this._inputLength - 1];
   }

   public void setCorrectionEnabled(boolean enable) {
      this._caseNeedsCorrection = enable;
   }

   public boolean isNeedCorrection() {
      return this._caseNeedsCorrection;
   }

   public void keysTyped(StringBuffer aKeyChars) {
      this.keysTyped(aKeyChars, aKeyChars.length());
   }

   public void correct(char[] aWord, int aOffset, int aLen) {
      int min_len = Math.min(this._inputLength, aLen);

      for (int i = 0; i < min_len; i++) {
         char ch = aWord[aOffset + i];
         boolean isUP = CharacterUtilities.isUpperCase(ch);
         if (this._userCaseSet[i] && !isUP) {
            aWord[aOffset + i] = CharacterUtilities.toUpperCase(ch, this._locale.getCode());
         }
      }

      min_len = Math.min(this._suffixSetLen, aLen - this._inputLength);

      for (int i = 0; i < min_len; i++) {
         char ch = aWord[aOffset + i + this._inputLength];
         boolean isUP = CharacterUtilities.isUpperCase(ch);
         if (isUP != this._suffixCaseSet[i]) {
            if (this._suffixCaseSet[i]) {
               aWord[aOffset + i + this._inputLength] = CharacterUtilities.toUpperCase(ch, this._locale.getCode());
            } else {
               aWord[aOffset + i + this._inputLength] = CharacterUtilities.toLowerCase(ch, this._locale.getCode());
            }
         }
      }
   }

   public void correct(StringBuffer aWord, int aOffset, int aLen) {
      char[] tmp = this.getTempCharBuffer(aLen);
      aWord.getChars(aOffset, aLen, tmp, 0);
      this.correct(tmp, 0, aLen);
      aWord.delete(aOffset, aOffset + aLen);
      if (tmp.length != aLen) {
         Array.resize(tmp, aLen);
      }

      aWord.insert(aOffset, tmp);
   }

   public void correct(ResultContainer aContainer) {
      if (this._caseNeedsCorrection && !aContainer.hasCaseCorrected()) {
         char[] words = aContainer.getWords();
         int count = aContainer.getVariantsCount();
         IntVector hs = (IntVector)(new Object(count));

         for (int i = 0; i < count; i++) {
            aContainer.getRefToVariantAt(i, this._correctionVariant);
            this.correct(words, this._correctionVariant._offset, this._correctionVariant._length);
            int hash = this._correctionVariant.hashCode();
            if (hs.indexOf(hash) == -1) {
               hs.addElement(hash);
            } else {
               aContainer.markForDelete(i);
            }
         }

         if (aContainer.hasDeleteMarks()) {
            aContainer.processDelete();
         }

         aContainer.calcHashCodesAndLen();
         aContainer.setCaseCorrectionIndicator(true);
      }
   }

   private char[] getTempCharBuffer(int size) {
      char[] result = (char[])this._tempCharBuffer.get();
      if (result == null) {
         result = new char[size];
         this._tempCharBuffer.set(result);
         return result;
      }

      if (result.length < size) {
         Array.resize(result, size);
      }

      return result;
   }

   public byte classifyCase() {
      if (this._allUpper) {
         return 1;
      }

      if (!this._caseNeedsCorrection) {
         return 0;
      }

      boolean is_uc = this._userCaseSet[0];
      boolean is_cap = is_uc;

      for (int i = 1; i < this._inputLength; i++) {
         boolean is_current_uc = this._userCaseSet[i];
         if (is_uc && !is_current_uc) {
            if (i != 1) {
               return 4;
            }

            is_uc = false;
         } else if (!is_uc && is_current_uc) {
            if (i != 1 && is_cap) {
               return 3;
            }

            return 4;
         }
      }

      return 2;
   }

   public boolean isAllLetters() {
      return this._allLetters;
   }

   public char toUpperCase(char aChar) {
      return toUpperCase(aChar, this._locale);
   }

   public char toLowerCase(char aChar) {
      return toLowerCase(aChar, this._locale);
   }

   public String toUpperCase(String aStr) {
      return toUpperCase(aStr, this._locale);
   }

   public String toLowerCase(String aStr) {
      return toLowerCase(aStr, this._locale);
   }

   public StringBuffer toLowerCase(StringBuffer aStr) {
      return this._caseNeedsCorrection ? toLowerCase(aStr, this._locale) : aStr;
   }

   public static char toUpperCase(char aChar, Locale aLocale) {
      if (aLocale == null) {
         aLocale = Locale.getDefaultInputForSystem();
      }

      return CharacterUtilities.toUpperCase(aChar, aLocale.getCode());
   }

   public static char toLowerCase(char aChar, Locale aLocale) {
      if (aLocale == null) {
         aLocale = Locale.getDefaultInputForSystem();
      }

      return CharacterUtilities.toLowerCase(aChar, aLocale.getCode());
   }

   public static String toUpperCase(String aStr, Locale aLocale) {
      if (aLocale == null) {
         aLocale = Locale.getDefaultInputForSystem();
      }

      return StringUtilities.toUpperCase(aStr, aLocale.getCode());
   }

   public static String toLowerCase(String aStr, Locale aLocale) {
      if (aLocale == null) {
         aLocale = Locale.getDefaultInputForSystem();
      }

      return StringUtilities.toLowerCase(aStr, aLocale.getCode());
   }

   public static void toUpperCase(char[] aStr, int aLen, Locale aLocale) {
      for (int i = 0; i < aLen; i++) {
         aStr[i] = toUpperCase(aStr[i], aLocale);
      }
   }

   public static void toUpperCase(StringBuffer aStr, Locale aLocale) {
      int len = aStr.length();

      for (int i = 0; i < len; i++) {
         aStr.setCharAt(i, toUpperCase(aStr.charAt(i), aLocale));
      }
   }

   public void toLowerCase(char[] aStr, int aOffset, int aLen) {
      toLowerCase(aStr, aOffset, aLen, this._locale);
   }

   public static StringBuffer toLowerCase(StringBuffer aStr, Locale aLocale) {
      int len = aStr.length();
      StringBuffer iTempBuffer = WeakReferenceUtilities.getStringBuffer(iTempBufferWR);
      iTempBuffer.setLength(len);

      for (int i = 0; i < len; i++) {
         iTempBuffer.setCharAt(i, toLowerCase(aStr.charAt(i), aLocale));
      }

      return iTempBuffer;
   }

   public static void toLowerCase(char[] aStr, int aOffset, int aLen, Locale aLocale) {
      int till = aLen + aOffset;

      for (int i = aOffset; i < till; i++) {
         aStr[i] = toLowerCase(aStr[i], aLocale);
      }
   }

   public static void toLowerCase(SLCurrentVariant aWord, Locale aLocale) {
      toLowerCase(aWord._variants, aWord._offset, aWord._length, aLocale);
   }

   public static byte classifyCase(String aWord) {
      boolean is_uc = CharacterUtilities.isUpperCase(aWord.charAt(0));
      boolean is_cap = is_uc;

      for (int i = 1; i < aWord.length(); i++) {
         boolean is_current_uc = CharacterUtilities.isUpperCase(aWord.charAt(i));
         if (is_uc && !is_current_uc) {
            if (i != 1) {
               return 4;
            }

            is_uc = false;
         } else if (!is_uc && is_current_uc) {
            if (i != 1 && is_cap) {
               return 3;
            }

            return 4;
         }
      }

      if (is_uc) {
         return 1;
      } else {
         return (byte)(is_cap ? 2 : 0);
      }
   }

   public static byte classifyCase(StringBuffer aWord) {
      boolean is_uc = CharacterUtilities.isUpperCase(aWord.charAt(0));
      boolean is_cap = is_uc;

      for (int i = 1; i < aWord.length(); i++) {
         boolean is_current_uc = CharacterUtilities.isUpperCase(aWord.charAt(i));
         if (is_uc && !is_current_uc) {
            if (i != 1) {
               return 4;
            }

            is_uc = false;
         } else if (!is_uc && is_current_uc) {
            if (i != 1 && is_cap) {
               return 3;
            }

            return 4;
         }
      }

      if (is_uc) {
         return 1;
      } else {
         return (byte)(is_cap ? 2 : 0);
      }
   }

   public static byte classifyCase(char[] aWord, int aOffset, int aLen) {
      boolean is_uc = CharacterUtilities.isUpperCase(aWord[aOffset]);
      boolean is_cap = is_uc;

      for (int i = 1; i < aLen; i++) {
         boolean is_current_uc = CharacterUtilities.isUpperCase(aWord[aOffset + i]);
         if (is_uc && !is_current_uc) {
            if (i != 1) {
               return 4;
            }

            is_uc = false;
         } else if (!is_uc && is_current_uc) {
            if (i != 1 && is_cap) {
               return 3;
            }

            return 4;
         }
      }

      if (is_uc) {
         return 1;
      } else {
         return (byte)(is_cap ? 2 : 0);
      }
   }

   public static byte classifyCase(StringBufferGap aWord, int aOffset, int aLen) {
      boolean is_uc = CharacterUtilities.isUpperCase(aWord.charAt(aOffset));
      boolean is_cap = is_uc;

      for (int i = 1; i < aLen; i++) {
         boolean is_current_uc = CharacterUtilities.isUpperCase(aWord.charAt(aOffset + i));
         if (is_uc && !is_current_uc) {
            if (i != 1) {
               return 4;
            }

            is_uc = false;
         } else if (!is_uc && is_current_uc) {
            if (i != 1 && is_cap) {
               return 3;
            }

            return 4;
         }
      }

      if (is_uc) {
         return 1;
      } else {
         return (byte)(is_cap ? 2 : 0);
      }
   }

   public static byte classifyCase(SLCurrentVariant aWord) {
      return classifyCase(aWord._variants, aWord._offset, aWord._length);
   }

   public void copyCaseSetTo(boolean[] result) {
      if (result.length < this._inputLength) {
         Array.resize(result, this._inputLength);
      }

      System.arraycopy(this._userCaseSet, 0, result, 0, this._inputLength);
   }

   public boolean[] getUserCaseSetRef() {
      return this._userCaseSet;
   }

   public char[] getUserInputForSCVariantsSearch() {
      return this._userInput;
   }
}
