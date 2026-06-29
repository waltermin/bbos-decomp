package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.wica.runtime.util.Util;

public class ScientificNumberTextFilter extends TextFilter {
   private final int _style;
   private String _text = "";
   private int _curPos = 0;
   public static final int ALLOW_NEGATIVE = 1;
   public static final int ALLOW_DECIMAL = 2;

   public ScientificNumberTextFilter() {
      this._style = 3;
   }

   @Override
   public boolean validate(AbstractString text) {
      return text != null && text.length() > 0 ? Util.isValidNumber(text.toString()) : true;
   }

   @Override
   public boolean validate(char character) {
      if (this.isValidChar(character)) {
         if (this._text.length() > this._curPos) {
            StringBuffer s = new StringBuffer(this._text);
            s.insert(this._curPos, character + "0");
            return Util.isValidNumber(s.toString());
         } else {
            return Util.isValidNumber(this._text + character + "0");
         }
      } else {
         return false;
      }
   }

   @Override
   public char convert(char character, int status) {
      if (!this.isValidChar(character)) {
         character = Keypad.getAltedChar(character);
      }

      return character;
   }

   public void setText(String text) {
      this._text = text == null ? "" : text;
   }

   public void setCursorPosition(int pos) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public long getPreferredInputStyle() {
      return 16777216;
   }

   private boolean isValidChar(char character) {
      return character >= '0' && character <= '9'
         || character == 128
         || character == '-' && (this._style & 1) != 0
         || character == '.' && (this._style & 2) != 0
         || character == 'E'
         || character == '+';
   }
}
