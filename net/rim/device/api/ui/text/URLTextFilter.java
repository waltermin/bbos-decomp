package net.rim.device.api.ui.text;

public class URLTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      if (character == ' ') {
         if ((status & 2) == 0) {
            return '.';
         }

         character = '/';
      }

      return character;
   }

   @Override
   public boolean validate(char character) {
      if (character > ' ' && 127 > character) {
         switch (character) {
            case '"':
            case '<':
            case '>':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '`':
            case '|':
               return false;
            default:
               return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public long getPreferredInputStyle() {
      return 117475328;
   }
}
