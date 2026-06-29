package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;

final class PhoneNumberInput$KeyEventInformation {
   private char _keyChar;
   private int _returnState;
   private final PhoneNumberInput this$0;
   private static final String validAltChars;

   public PhoneNumberInput$KeyEventInformation(PhoneNumberInput _1) {
      this.this$0 = _1;
      this.reset('\u0000');
   }

   private final void reset(char key) {
      this._keyChar = key;
      this._returnState = -1;
   }

   public final int getResultState() {
      return this._returnState;
   }

   public final char getEchoKey() {
      return this._keyChar;
   }

   public final void setInfo(char key, int state) {
      this.reset(key);
      char altKey = Keypad.getAltedChar(this._keyChar);
      if (altKey == 0) {
         altKey = this._keyChar;
         this._keyChar = Keypad.getUnaltedChar(this._keyChar);
         if (this._keyChar == 0) {
            this._keyChar = altKey;
         } else if (this.this$0._keypadAltMode) {
            this._keyChar = CharacterUtilities.toUpperCase(this._keyChar);
         } else {
            this._keyChar = CharacterUtilities.toLowerCase(this._keyChar);
         }
      } else if (altKey == '＃') {
         altKey = '#';
      }

      if (key != '\b' && key != 127 && altKey != 0) {
         if (!PhoneNumberInput._isReducedKeyboard) {
            switch (this.this$0._fieldState) {
               case -1:
                  break;
               case 0:
               default:
                  if (this._keyChar == '\uf3fe' || this._keyChar == '\uf402' || this._keyChar == '0') {
                     this._returnState = 1;
                     return;
                  }

                  if (CharacterUtilities.isUpperCase(this._keyChar)) {
                     if (this._keyChar == 'X' && this.this$0.getTextLength() == 0) {
                        this._keyChar = 'x';
                        this._returnState = state;
                        return;
                     }

                     if (this.this$0.getTextLength() == 0) {
                        this._returnState = 2;
                        return;
                     }

                     this._returnState = state;
                     return;
                  }

                  if (this.this$0.getTextLength() == 0 && this._keyChar == ' ') {
                     return;
                  }

                  if ("01234567890+*#\uf3fe\uf402".indexOf(altKey) != -1) {
                     this._keyChar = altKey;
                     this._returnState = this.this$0._fieldState;
                     return;
                  }
               case 2:
                  if (this._keyChar != '$' && this._keyChar != 8364 && this._keyChar != 165) {
                     this._returnState = 2;
                     return;
                  }

                  return;
               case 1:
                  if (CharacterUtilities.isUpperCase(this._keyChar)) {
                     this._returnState = state;
                     return;
                  }

                  if ("01234567890+*#\uf3fe\uf402".indexOf(altKey) != -1) {
                     this._keyChar = altKey;
                     this._returnState = state;
                     return;
                  }
            }
         } else {
            switch (state) {
               case -1:
                  break;
               case 0:
               default:
                  if (altKey == '#' || altKey == '*' || altKey == '+') {
                     this._keyChar = altKey;
                     this._returnState = 1;
                     return;
                  }

                  if (this._keyChar == '\uf3fe' || this._keyChar == '\uf402') {
                     this._returnState = 1;
                     return;
                  }

                  if (this._keyChar == ' ') {
                     this._keyChar = altKey;
                     if (this.this$0.getTextLength() == 0) {
                        this._returnState = 1;
                        return;
                     }

                     this._returnState = state;
                     return;
                  }

                  if (this._keyChar == CharacterUtilities.toLowerCase(Keypad.getUnaltedChar('@'))
                     || this._keyChar == CharacterUtilities.toLowerCase(Keypad.getUnaltedChar('\u0080'))
                     || this._keyChar == 'x'
                     || this._keyChar == 'z') {
                     this._keyChar = 'x';
                     this._returnState = state;
                     return;
                  }

                  if (CharacterUtilities.isUpperCase(this._keyChar)) {
                     if (this.this$0.getTextLength() == 0) {
                        this._returnState = 2;
                        return;
                     }

                     this._returnState = state;
                     return;
                  }

                  if ("01234567890+*#\uf3fe\uf402".indexOf(altKey) != -1) {
                     this._keyChar = altKey;
                     this._returnState = state;
                     return;
                  }
               case 2:
                  if (CharacterUtilities.isLetter(this._keyChar) || CharacterUtilities.isSpaceChar(this._keyChar)) {
                     this._returnState = 2;
                  }

                  return;
               case 1:
                  if (this._keyChar == CharacterUtilities.toLowerCase(Keypad.getUnaltedChar('@'))
                     || this._keyChar == CharacterUtilities.toLowerCase(Keypad.getUnaltedChar('\u0080'))
                     || this._keyChar == 'x'
                     || this._keyChar == 'z') {
                     this._keyChar = 'x';
                     this._returnState = state;
                     return;
                  }

                  if (CharacterUtilities.isUpperCase(this._keyChar)) {
                     this._returnState = state;
                     return;
                  }

                  if ("01234567890+*#\uf3fe\uf402".indexOf(altKey) != -1) {
                     this._keyChar = altKey;
                     this._returnState = state;
                     return;
                  }
            }
         }
      } else {
         this._returnState = state;
      }
   }
}
