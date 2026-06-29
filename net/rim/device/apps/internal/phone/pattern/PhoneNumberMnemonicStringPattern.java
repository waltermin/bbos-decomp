package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public final class PhoneNumberMnemonicStringPattern extends StringPattern {
   private WeakReference _scanbufferWR = new WeakReference(null);
   private WeakReference _strWR = new WeakReference(null);
   private int _state;
   private int _beginIndex;
   private int _endIndex;
   private int _digitCount;
   private int _letterCount;
   private int _runCount;
   private static final int MINIMUM_LEAD_DIGIT_COUNT = 4;
   private static final int MINIMUM_LETTER_COUNT = 2;
   private static final int MAXIMUM_RUN_COUNT = 5;
   private static final int SCAN_BUFFER_SIZE = 32;
   static final int STATE_VALID_BEGIN = 0;
   static final int STATE_INVALID_A = 1;
   static final int STATE_INVALID_B = 2;
   static final int STATE_RUN = 3;
   static final int STATE_SEPARATOR_A = 4;
   static final int STATE_SEPARATOR_B = 5;
   static final int STATE_SEPARATOR_C = 6;
   static final int STATE_FOUND_MATCH = 7;

   @Override
   public final long getPatternTypeIdentifier() {
      return 3797587162219887872L;
   }

   @Override
   public final synchronized boolean findMatch(AbstractString str, int index, int maxIndex, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      char[] scanbuffer = WeakReferenceUtilities.getCharArray(this._scanbufferWR, 32);
      this._strWR.set(str);
      this.reset(0);

      label124:
      while (index < maxIndex) {
         int count = maxIndex - index;
         if (count > scanbuffer.length) {
            count = scanbuffer.length;
         }

         str.getChars(index, index + count, scanbuffer, 0);

         for (int bufIndex = 0; bufIndex < count; index++) {
            char ch = scanbuffer[bufIndex];
            label118:
            switch (this._state) {
               case -1:
                  break;
               case 0:
               default:
                  switch (ch) {
                     case '\n':
                     case '!':
                     case '"':
                     case '(':
                     case ')':
                     case ',':
                     case '-':
                     case '.':
                     case '?':
                     case '[':
                     case ']':
                     case ' ':
                     case '\u200b':
                        break label118;
                     case '+':
                        this._beginIndex = index;
                        this._state = 3;
                        break label118;
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
                        this._beginIndex = index;
                        this._digitCount++;
                        this._state = 3;
                        break label118;
                     default:
                        this._state = 1;
                        break label118;
                  }
               case 1:
                  if (ch == '\n') {
                     this._state = 0;
                  } else if (ch >= 'a' && ch <= 'z') {
                     this._state = 2;
                  }
                  break;
               case 2:
                  switch (ch) {
                     case '\n':
                        this._state = 0;
                        break label118;
                     case ' ':
                        this._state = 0;
                     default:
                        break label118;
                  }
               case 3:
                  switch (ch) {
                     case '\n':
                     case '!':
                     case '"':
                     case '(':
                     case ')':
                     case ',':
                     case '?':
                     case '[':
                     case ']':
                        this.validateOrReset(ch);
                        break label118;
                     case ' ':
                        if (this._runCount >= 5) {
                           this.reset(1);
                        } else {
                           this._runCount++;
                           this._state = 4;
                        }
                        break label118;
                     case '-':
                     case '.':
                     case ' ':
                     case '\u200b':
                        if (this._runCount >= 5) {
                           this.reset(1);
                        } else {
                           this._runCount++;
                           this._state = 5;
                        }
                        break label118;
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
                        this._digitCount++;
                        this._endIndex = index + 1;
                        break label118;
                     default:
                        if (ch < 'A' || ch > 'Z') {
                           this.reset(1);
                        } else if (this._letterCount == 0 && this._digitCount < 4) {
                           this.reset(1);
                        } else {
                           this._letterCount++;
                           this._endIndex = index + 1;
                        }
                        break label118;
                  }
               case 4:
               case 5:
               case 6:
                  switch (ch) {
                     case ' ':
                        if (this._state == 5) {
                           this._state = 6;
                        } else {
                           this.validateOrReset(ch);
                        }
                        break;
                     case '-':
                     case '.':
                     case ' ':
                     case '\u200b':
                        if (this._state == 4) {
                           this._state = 5;
                        } else {
                           this.validateOrReset(ch);
                        }
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
                        this._digitCount++;
                        this._endIndex = index + 1;
                        this._state = 3;
                        break;
                     default:
                        if (ch < 'A' || ch > 'Z') {
                           this.validateOrReset(ch);
                        } else if (this._letterCount == 0 && this._digitCount < 4) {
                           this.reset(1);
                        } else {
                           this._letterCount++;
                           this._endIndex = index + 1;
                           this._state = 3;
                        }
                  }
            }

            if (this._state == 7) {
               break label124;
            }

            bufIndex++;
         }
      }

      if (index >= maxIndex) {
         if (this._state == 3 && this.validate()) {
            this._state = 7;
         } else {
            this._state = 1;
         }
      }

      if (this._state == 7) {
         match.id = 3797587162219887872L;
         match.beginIndex = this._beginIndex;
         match.endIndex = this._endIndex;
         match.endIndex = match.endIndex + PhoneNumberRawStringPattern.getExtensionLength(str, match.endIndex, maxIndex);
         match.prefixLength = 0;
         return true;
      } else {
         return false;
      }
   }

   final void reset(int state) {
      this._state = state;
      this._beginIndex = 0;
      this._endIndex = 0;
      this._digitCount = 0;
      this._letterCount = 0;
      this._runCount = 0;
   }

   private final void validateOrReset(char ch) {
      if (this.validate()) {
         this._state = 7;
      } else if (ch == '\n') {
         this.reset(2);
      } else {
         this.reset(1);
      }
   }

   private final boolean validate() {
      if (this._letterCount < 2) {
         return false;
      }

      int count = this._digitCount + this._letterCount;
      AbstractString str = (AbstractString)this._strWR.get();
      int cc = WorldPhoneInfo.parseCountryCode(str, this._beginIndex);
      int ccCount;
      if (cc < 10) {
         ccCount = 1;
      } else if (cc < 100) {
         ccCount = 2;
      } else {
         ccCount = 3;
      }

      int[] nnl = WorldPhoneInfo.getNationalPhoneNumberLengthRange(cc);
      if (nnl != null && ccCount + nnl[0] <= count && count <= ccCount + nnl[1]) {
         return true;
      }

      if (str.charAt(this._beginIndex) != '+') {
         cc = SmartDialingOptions.getOptions().getCountryCode();
         nnl = WorldPhoneInfo.getNationalPhoneNumberLengthRange(cc);
         if (nnl != null) {
            if (nnl[0] <= count && count <= nnl[1]) {
               return true;
            }

            char[] ndd = WorldPhoneInfo.getNationalDialingDigits(cc);
            if (ndd != null) {
               int len = ndd.length;
               if (len + nnl[0] <= count && count <= len + nnl[1] && compare(ndd, str, this._beginIndex)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static final boolean compare(char[] strA, AbstractString strB, int offsetB) {
      int len = strA.length;

      while (len > 0) {
         len--;
         if (strA[len] != strB.charAt(offsetB + len)) {
            return false;
         }
      }

      return true;
   }
}
