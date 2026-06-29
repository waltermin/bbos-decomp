package net.rim.device.api.ui.text;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;

public class IPTextFilter extends TextFilter {
   private int _flags;
   public static final int NO_PORTS = 1;
   public static final int NUMERIC_IP_ONLY = 2;

   public IPTextFilter() {
   }

   public IPTextFilter(int flag) {
      this._flags = flag;
   }

   @Override
   public char convert(char character, int status) {
      if (!this.validate(character)) {
         if ((this._flags & 2) != 0) {
            return Keypad.getAltedChar(character);
         }

         character = CharacterUtilities.toLowerCase(character, 1701707776);
      }

      return character;
   }

   @Override
   public long getPreferredInputStyle() {
      return 49152 | ((this._flags & 2) != 0 ? 5 : 1) << 24;
   }

   @Override
   public boolean validate(AbstractString text) {
      if (text != null && text.length() != 0) {
         int textLength = text.length();
         int pointCounter = 0;
         int colonCounter = 0;
         boolean containsLetters = false;

         for (int lv = 0; lv < textLength; lv++) {
            char ch = text.charAt(lv);
            if (Character.isLowerCase(ch) || Character.isUpperCase(ch)) {
               if ((this._flags & 2) != 0) {
                  return false;
               }

               containsLetters = true;
               if (colonCounter > 0) {
                  return false;
               }
            }

            if (!this.validate(ch)) {
               return false;
            }

            if (ch == '.') {
               if (++pointCounter > 3 && !containsLetters) {
                  return false;
               }
            } else if (ch == ':') {
               if ((this._flags & 1) != 0) {
                  return false;
               }

               if (++colonCounter > 2) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean validate(char character) {
      if (character == ':') {
         return (this._flags & 1) == 0;
      }

      if (Character.isDigit(character)) {
         return true;
      }

      if ((this._flags & 2) != 0) {
         if (character == '.') {
            return true;
         }
      } else if (Character.isLowerCase(character) || character == '-' || character == '.') {
         return true;
      }

      return false;
   }
}
