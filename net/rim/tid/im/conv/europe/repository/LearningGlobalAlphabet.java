package net.rim.tid.im.conv.europe.repository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.repository.AlphabetChangeListener;

public class LearningGlobalAlphabet implements ReaderConstants {
   protected byte[] _data;
   protected int _startOffset;
   protected String _alphabet = "";
   private Locale _locale;
   private boolean _caseSensitive;
   private int _updatedLen;
   private boolean _overrideListenersMode;
   private AlphabetChangeListener _listener1;
   private AlphabetChangeListener _listener2;

   public LearningGlobalAlphabet(boolean aCaseSensitive) {
      this._caseSensitive = aCaseSensitive;
      this.init();
   }

   public void init(byte[] aData, int aOffset) {
      this._data = aData;
      this._startOffset = aOffset;
   }

   public void read(DataInputStream aDos) {
      int len = aDos.readByte() & 255;
      char[] data = new char[len];

      for (int i = 0; i < len; i++) {
         data[i] = aDos.readChar();
      }

      this._alphabet = new String(data);
      this._updatedLen = len;
      this.fireAlphabetChangedEvent();
   }

   public void write(DataOutputStream dos) {
      dos.writeByte(this._alphabet.length());
      dos.writeChars(this._alphabet);
   }

   public void update() {
      int new_len = this._alphabet.length();
      if (this._updatedLen != new_len) {
         int offset = this._startOffset;
         this._data[offset++] = (byte)new_len;
         offset += 2 * this._updatedLen;

         for (int i = this._updatedLen; i < new_len; i++) {
            char ch = this._alphabet.charAt(i);
            this._data[offset++] = (byte)(ch >> '\b');
            this._data[offset++] = (byte)ch;
         }

         this._updatedLen = new_len;
         this.fireAlphabetChangedEvent();
      }
   }

   public int size() {
      return this._alphabet.length() * 2 + 1;
   }

   public int encodeWord(StringBuffer aWord, byte[] aEncodedWord, int aOffset, boolean aConvertToLowerCase) {
      return this.encodeWord(aWord, aEncodedWord, aOffset, aConvertToLowerCase, true);
   }

   public int encodeWord(StringBuffer aWord, byte[] aEncodedWord, int aOffset, boolean aConvertToLowerCase, boolean aGrowIfNeeded) {
      int len = aWord.length();
      int encoded_len = 0;
      byte case_type = 0;
      boolean is_uc = false;
      if (this._caseSensitive && !aConvertToLowerCase) {
         case_type = CaseCorrector.classifyCase(aWord);
         is_uc = case_type == 1;
      }

      String origAlphabet = this._alphabet;

      while (encoded_len < len) {
         char ch = aWord.charAt(encoded_len);
         if (aConvertToLowerCase || this._caseSensitive && (encoded_len < 2 || is_uc)) {
            ch = CaseCorrector.toLowerCase(ch, this._locale);
         }

         int pos = this.indexOf(ch);
         if (pos == -1) {
            if (!aGrowIfNeeded) {
               return -1;
            }

            pos = this._alphabet.length();
            if (pos == 127) {
               this._alphabet = origAlphabet;
               return -1;
            }

            this._alphabet = this._alphabet + ch;
         }

         aEncodedWord[aOffset + encoded_len++] = (byte)pos;
      }

      if (this._caseSensitive && case_type != 0) {
         if (is_uc) {
            aEncodedWord[aOffset + encoded_len++] = 2;
         } else if (case_type == 2 || case_type == 3) {
            aEncodedWord[aOffset + encoded_len++] = 1;
         } else if (CharacterUtilities.isUpperCase(aWord.charAt(0))) {
            aEncodedWord[aOffset + encoded_len++] = 4;
         } else if (CharacterUtilities.isUpperCase(aWord.charAt(1))) {
            aEncodedWord[aOffset + encoded_len++] = 3;
         }
      }

      return aOffset + encoded_len;
   }

   public int encodeWord(SLCurrentVariant aWord, byte[] aEncodedWord, int aOffset, boolean aConvertToLowerCase) {
      return this.encodeWord(aWord, aEncodedWord, aOffset, aConvertToLowerCase, true);
   }

   public int encodeWord(SLCurrentVariant aWord, byte[] aEncodedWord, int aOffset, boolean aConvertToLowerCase, boolean aGrowIfNeeded) {
      int len = aWord._length;
      int encoded_len = 0;
      byte case_type = 0;
      boolean is_uc = false;
      char[] word = aWord._variants;
      int offset = aWord._offset;
      if (this._caseSensitive && !aConvertToLowerCase) {
         case_type = CaseCorrector.classifyCase(aWord);
         is_uc = case_type == 1;
      }

      String origAlphabet = this._alphabet;

      while (encoded_len < len) {
         char ch = word[offset + encoded_len];
         if (aConvertToLowerCase || this._caseSensitive && (encoded_len < 2 || is_uc)) {
            ch = CaseCorrector.toLowerCase(ch, this._locale);
         }

         int pos = this.indexOf(ch);
         if (pos == -1) {
            if (!aGrowIfNeeded) {
               return -1;
            }

            pos = this._alphabet.length();
            if (pos == 127) {
               this._alphabet = origAlphabet;
               return -1;
            }

            this._alphabet = this._alphabet + ch;
         }

         aEncodedWord[aOffset + encoded_len++] = (byte)pos;
      }

      if (this._caseSensitive && case_type != 0) {
         if (is_uc) {
            aEncodedWord[aOffset + encoded_len++] = 2;
         } else if (case_type == 2 || case_type == 3) {
            aEncodedWord[aOffset + encoded_len++] = 1;
         } else if (CharacterUtilities.isUpperCase(word[0])) {
            aEncodedWord[aOffset + encoded_len++] = 4;
         } else if (CharacterUtilities.isUpperCase(word[1])) {
            aEncodedWord[aOffset + encoded_len++] = 3;
         }
      }

      return aOffset + encoded_len;
   }

   public int getAlphabetGrowth(SLCurrentVariant aWord) {
      int count = 0;
      int end = aWord._offset + aWord._length;

      for (int i = aWord._offset; i < end; i++) {
         char ch = aWord._variants[i];
         if (this._alphabet.indexOf(ch, 5) == -1) {
            count++;
         }
      }

      return count * 2;
   }

   public void setLocale(Locale aLocale) {
      this._locale = aLocale;
   }

   public void init() {
      if (this._caseSensitive) {
         this._alphabet = "";
      } else {
         this._alphabet = "";
      }

      this._updatedLen = this._alphabet.length();
   }

   public char charAt(int index) {
      return this._alphabet.charAt(index);
   }

   public int indexOf(int ch) {
      int start = 0;
      if (this._caseSensitive) {
         start = 5;
      }

      return this._alphabet.indexOf(ch, start);
   }

   public int length() {
      return this._alphabet.length();
   }

   @Override
   public boolean equals(Object anObject) {
      return this._alphabet.equals(anObject);
   }

   public void addAlphabetChangeListener(AlphabetChangeListener l) {
      if (this._listener1 == null || l == this._listener1) {
         this._listener1 = l;
      } else {
         if (!this._overrideListenersMode && this._listener2 != null && l != this._listener2) {
            throw new IllegalStateException("Only two alphabet change listeners are supported!");
         }

         this._listener2 = l;
      }
   }

   public void removeAlphabetChangeListener(AlphabetChangeListener l) {
      if (this._listener1 == l) {
         this._listener1 = null;
      } else {
         if (this._listener2 == l) {
            this._listener2 = null;
         }
      }
   }

   private void fireAlphabetChangedEvent() {
      if (this._listener1 != null) {
         this._listener1.alphabetChanged();
      }

      if (this._listener2 != null) {
         this._listener2.alphabetChanged();
      }
   }

   public void setOverrideListenersMode(boolean isEnabled) {
      this._overrideListenersMode = isEnabled;
   }
}
