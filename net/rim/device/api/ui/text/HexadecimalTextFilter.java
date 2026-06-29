package net.rim.device.api.ui.text;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;

public class HexadecimalTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      if (InputContext.getInstance().isSureType()) {
         if (Character.isUpperCase(character) && this.validate(character)) {
            return character;
         }

         if ((status & 1) != 0) {
            SLKeyLayout layout = Keypad.getLayout();
            int code = layout.getOriginalKeyCode(character, SLKeyLayout.convertStatusToModifiers(status));
            character = Keypad.map(code, 2);
            status = 0;
         }
      }

      if ((status & 1) != 0) {
         char newChar = CharacterUtilities.toUpperCase(Keypad.getUnaltedChar(character), 1701707776);
         if (this.validate(newChar)) {
            return newChar;
         }
      }

      if ((status & 32768) != 0) {
         return CharacterUtilities.toUpperCase(character, 1701707776);
      }

      if (this.validate(character)) {
         return character;
      }

      char newChar = Keypad.getAltedChar(character);
      return this.validate(newChar) ? newChar : CharacterUtilities.toUpperCase(character, 1701707776);
   }

   @Override
   public boolean validate(char character) {
      return character >= '0' && character <= '9' || character >= 'A' && character <= 'F';
   }

   @Override
   public long getPreferredInputStyle() {
      return 1140850688;
   }
}
