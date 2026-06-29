package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

public final class SimplePhoneNumberFilter extends TextFilter {
   private int _flags;
   public static final int ACCEPTS_STAR;
   public static final int ACCEPTS_POUND;
   public static final int ACCEPTS_PAUSE_AND_WAIT;
   public static final int ACCEPTS_STAR_POUND_PAUSE_AND_WAIT;
   public static final int NO_PLUS_SIGN;

   public SimplePhoneNumberFilter() {
      this(0);
   }

   public SimplePhoneNumberFilter(int flags) {
      this._flags = flags;
   }

   @Override
   public final char convert(char character, int status) {
      if ((status & 1) == 0 || !PhoneUtilities.isQwertyReducedKeyboard()) {
         character = this.checkForDTMF(character);
         if (!this.validate(character)) {
            character = this.checkForDTMF(Keypad.getAltedChar(character));
            if (!this.validate(character)) {
               character = '\u0000';
            }
         }

         return character;
      } else {
         if (character == ' ') {
            return character;
         }

         character = Keypad.getUnaltedChar(character);
         return Keypad.map(character, 2);
      }
   }

   final char checkForDTMF(char in) {
      if (in == '!') {
         return '\uf402';
      } else {
         return in == ',' ? '\uf3fe' : in;
      }
   }

   @Override
   public final boolean validate(char character) {
      switch (character) {
         case '#':
            if ((this._flags & 4) != 0) {
               return true;
            }

            return false;
         case '*':
            if ((this._flags & 2) != 0) {
               return true;
            }

            return false;
         case '+':
            if ((this._flags & 16) == 0) {
               return true;
            }

            return false;
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
            return true;
         case '\uf3fe':
         case '\uf402':
            if ((this._flags & 8) != 0) {
               return true;
            }

            return false;
         default:
            return false;
      }
   }

   @Override
   public final boolean validate(AbstractString text) {
      if (text != null) {
         int textLength = text.length();

         for (int i = 0; i < textLength; i++) {
            char c = text.charAt(i);
            if (!this.validate(c)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public final long getPreferredInputStyle() {
      return 1174405120;
   }
}
