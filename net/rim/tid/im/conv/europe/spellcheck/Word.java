package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.im.conv.europe.AccentGrouping;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.vm.Array;

public class Word implements AbstractString {
   public int start;
   public int end;
   public char[] text;
   public boolean containsUpper;
   public boolean containsLower;
   public boolean containsNum;
   public boolean containsAlpha;
   public boolean containsNonAlphaNum;
   public boolean containsAccents;
   public boolean isCapitalized;
   char[] _buffer = new char[50];
   ChildWord _upper;
   boolean _upperInitialized;
   ChildWord _lower;
   boolean _lowerInitialized;
   Locale _locale;

   public Locale getLocale() {
      return this._locale;
   }

   public boolean equalsIgnoreCase(String str) {
      int strLen = str.length();
      Word upper = this.getUpper();
      Word lower = this.getLower();

      for (int i = 0; i < strLen; i++) {
         char ch = str.charAt(i);
         if (ch != upper.charAt(i) && ch != lower.charAt(i)) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(String str) {
      int strLen = str.length();
      if (strLen != this.end - this.start) {
         return false;
      }

      for (int i = 0; i < strLen; i++) {
         if (str.charAt(i) != this.text[this.start + i]) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(Word oword) {
      int oLen = oword.length();
      if (oLen != this.end - this.start) {
         return false;
      }

      for (int i = 0; i < oLen; i++) {
         if (oword.charAt(i) != this.text[this.start + i]) {
            return false;
         }
      }

      return true;
   }

   public void init(AbstractString str, int start, int end, Locale locale) {
      int len = end - start;
      if (len > this._buffer.length) {
         Array.resize(this._buffer, len);
      }

      str.getChars(start, end, this._buffer, 0);
      this.init(this._buffer, 0, len, locale);
   }

   public void init(char[] text, int start, int end, Locale locale) {
      this.text = text;
      this.start = start;
      this.end = end;
      this._locale = locale;
      this.containsUpper = false;
      this.containsLower = false;
      this.containsNum = false;
      this.containsAlpha = false;
      this.containsNonAlphaNum = false;
      this.containsAccents = false;
      this.isCapitalized = false;
      this._upperInitialized = false;
      this._lowerInitialized = false;

      for (int i = start; i < end; i++) {
         char ch = text[i];
         if (CharacterUtilities.isLetter(ch)) {
            this.containsAlpha = true;
            if (CharacterUtilities.isUpperCase(ch)) {
               this.containsUpper = true;
               if (i == start) {
                  this.isCapitalized = true;
               }
            } else {
               this.containsLower = true;
            }

            if (AccentGrouping.isAccented(ch, locale)) {
               this.containsAccents = true;
            }
         } else if (CharacterUtilities.isDigit(ch)) {
            this.containsNum = true;
         } else {
            this.containsNonAlphaNum = true;
         }
      }
   }

   public Word subword(int beginIndex, int endIndex, Word dest) {
      if (dest == null) {
         dest = new Word();
      }

      if (this.start + endIndex > this.end) {
         throw new Object();
      }

      dest.init(this.text, this.start + beginIndex, this.start + endIndex, this._locale);
      if (this._upperInitialized) {
         dest._upperInitialized = true;
         dest.toUpper(this._upper.text, this._upper.start + beginIndex);
      }

      if (this._lowerInitialized) {
         dest._lowerInitialized = true;
         dest.toLower(this._lower.text, this._lower.start + beginIndex);
      }

      return dest;
   }

   public final Word getUpper() {
      if (this._upperInitialized) {
         return this._upper;
      }

      this.toUpper();
      return this._upper;
   }

   public final Word getLower() {
      if (this._lowerInitialized) {
         return this._lower;
      }

      this.toLower();
      return this._lower;
   }

   public final Word toAccentless(Word dest) {
      if (dest == null) {
         if (!this.containsAccents) {
            return this;
         }

         dest = new Word();
      }

      int len = this.end - this.start;
      if (len > dest._buffer.length) {
         Array.resize(dest._buffer, len);
      }

      for (int i = 0; i < len; i++) {
         dest._buffer[i] = AccentGrouping.toAccentless(this.charAt(i), this._locale);
      }

      dest.init(dest._buffer, 0, len, this._locale);
      return dest;
   }

   @Override
   public int length() {
      return this.end - this.start;
   }

   @Override
   public int indexOf(char ch, int startIndex, int endIndex) {
      endIndex += this.start;
      if (endIndex > this.end) {
         endIndex = this.end;
      }

      for (int i = this.start + startIndex; i < endIndex; i++) {
         if (this.text[i] == ch) {
            return i - this.start;
         }
      }

      return -1;
   }

   @Override
   public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      System.arraycopy(this.text, srcBegin + this.start, dst, dstBegin, srcEnd - srcBegin);
   }

   @Override
   public char charAt(int index) {
      return this.text[index + this.start];
   }

   private void finiToUpper() {
      this._upper._locale = this._locale;
      this._upper.containsUpper = this.containsUpper;
      this._upper.containsLower = false;
      this._upper.containsNum = this.containsNum;
      this._upper.containsAlpha = this.containsAlpha;
      this._upper.containsNonAlphaNum = this.containsNonAlphaNum;
      this._upper._upper = this._upper;
      this._upper._upperInitialized = true;
      this._upper._lowerInitialized = false;
      this._upperInitialized = true;
   }

   private void toUpper(char[] upperText, int upperOffset) {
      if (this._upper == null) {
         this._upper = new ChildWord();
      }

      this._upper.text = upperText;
      this._upper.start = upperOffset;
      this._upper.end = upperOffset + this.end - this.start;
      this._upper.isCapitalized = true;
      this.finiToUpper();
   }

   private void toUpper() {
      if (this._upper == null) {
         this._upper = new ChildWord();
      }

      this._upperInitialized = true;
      if (this.containsLower) {
         if (this._upper._buffer.length < this.end - this.start) {
            Array.resize(this._upper._buffer, this.end - this.start);
         }

         this._upper.text = this._upper._buffer;
         this._upper.start = 0;
         this._upper.end = this.end - this.start;
      } else {
         this._upper.text = this.text;
         this._upper.start = this.start;
         this._upper.end = this.end;
      }

      int diff = this._upper.start - this.start;

      for (int i = this.start; i < this.end; i++) {
         char ch = this.text[i];
         if (i == this.start && CharacterUtilities.isLetter(ch)) {
            this._upper.isCapitalized = true;
         }

         this._upper.text[diff + i] = CaseCorrector.toUpperCase(ch, this._locale);
      }

      this.finiToUpper();
   }

   private void finiToLower() {
      this._lower._locale = this._locale;
      this._lower.containsLower = this.containsLower;
      this._lower.containsUpper = false;
      this._lower.containsNum = this.containsNum;
      this._lower.containsAlpha = this.containsAlpha;
      this._lower.containsNonAlphaNum = this.containsNonAlphaNum;
      this._lower.isCapitalized = false;
      this._lower._lower = this._lower;
      this._lower._lowerInitialized = true;
      this._lower._upperInitialized = false;
      this._lowerInitialized = true;
   }

   private void toLower(char[] lowerText, int lowerOffset) {
      if (this._lower == null) {
         this._lower = new ChildWord();
      }

      this._lower.text = lowerText;
      this._lower.start = lowerOffset;
      this._lower.end = lowerOffset + this.end - this.start;
      this.finiToLower();
   }

   private void toLower() {
      if (this._lower == null) {
         this._lower = new ChildWord();
      }

      this._lowerInitialized = true;
      if (this.containsUpper) {
         if (this._lower._buffer.length < this.end - this.start) {
            Array.resize(this._lower._buffer, this.end - this.start);
         }

         this._lower.text = this._lower._buffer;
         this._lower.start = 0;
         this._lower.end = this.end - this.start;
      } else {
         this._lower.text = this.text;
         this._lower.start = this.start;
         this._lower.end = this.end;
      }

      int diff = this._lower.start - this.start;

      for (int i = this.start; i < this.end; i++) {
         char ch = this.text[i];
         this._lower.text[diff + i] = CaseCorrector.toLowerCase(ch, this._locale);
      }

      this.finiToLower();
   }
}
