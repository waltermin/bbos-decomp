package net.rim.device.apps.internal.sms;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.text.TextFilter;

final class SIMATNumericFilter extends TextFilter {
   private boolean _allowPlus;

   SIMATNumericFilter(boolean allowPlus) {
      this._allowPlus = allowPlus;
   }

   @Override
   public final char convert(char character, int status) {
      if (!this.validate(character) && character != 128) {
         character = Keypad.getAltedChar(character);
      }

      return character;
   }

   @Override
   public final boolean validate(char character) {
      if ('0' <= character && character <= '9') {
         return true;
      }

      switch (character) {
         case '+':
            if (!this._allowPlus) {
               return false;
            }
         case '#':
         case '*':
            return true;
         default:
            return false;
      }
   }
}
