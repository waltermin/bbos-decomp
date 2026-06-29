package net.rim.device.api.ui.text;

import net.rim.device.api.util.AbstractString;

public class EmailAddressTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      return character;
   }

   @Override
   public boolean validate(char character) {
      if (character <= 31) {
         return false;
      }

      switch (character) {
         case '"':
         case '<':
         case '>':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case '{':
         case '|':
         case '}':
         case '\u007f':
            return false;
         default:
            return true;
      }
   }

   @Override
   public boolean validate(AbstractString text) {
      if (text != null) {
         int textLength = text.length();

         for (int lv = 0; lv < textLength; lv++) {
            char character = text.charAt(lv);
            if (!this.validate(character)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public long getPreferredInputStyle() {
      return 134266880;
   }
}
