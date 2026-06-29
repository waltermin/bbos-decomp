package net.rim.device.api.ui.text;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.AbstractString;

public class NumericTextFilter extends TextFilter {
   private final int _style;
   public static final int ALLOW_NEGATIVE = 1;
   public static final int ALLOW_DECIMAL = 2;

   public NumericTextFilter() {
      this(0);
   }

   public NumericTextFilter(int style) {
      this._style = style;
   }

   @Override
   public char convert(char character, int status) {
      if (!this.validate(character)) {
         character = Keypad.getAltedChar(character);
      }

      return character;
   }

   @Override
   public char convert(char character, AbstractString text, int position, int status) {
      if (!this.validate(character, text, position)) {
         character = Keypad.getAltedChar(character);
      }

      return character;
   }

   @Override
   public boolean validate(char character) {
      return character >= '0' && character <= '9'
         || character == 128
         || character == '-' && (this._style & 1) != 0
         || character == '.' && (this._style & 2) != 0;
   }

   @Override
   public boolean validate(char character, AbstractString text, int position) {
      return text == null
         || character >= '0' && character <= '9'
         || character == 128
         || character == '-' && (this._style & 1) != 0 && position == 0 && (text.length() == 0 || text.charAt(0) != '-')
         || character == '.' && (this._style & 2) != 0;
   }

   @Override
   public boolean validate(AbstractString text) {
      if (text != null) {
         int textLength = text.length();

         for (int lv = 0; lv < textLength; lv++) {
            char character = text.charAt(lv);
            if (character == '-' && (lv != 0 || (this._style & 1) == 0)) {
               return false;
            }

            if (!this.validate(character)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public long getPreferredInputStyle() {
      return 1090519040;
   }
}
