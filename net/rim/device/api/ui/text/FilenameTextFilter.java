package net.rim.device.api.ui.text;

public class FilenameTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      return character;
   }

   @Override
   public boolean validate(char character) {
      switch (character) {
         case '"':
         case '*':
         case '/':
         case ':':
         case '<':
         case '>':
         case '?':
         case '\\':
         case '|':
            return false;
         default:
            return character >= ' ';
      }
   }

   @Override
   public long getPreferredInputStyle() {
      return 268470272;
   }
}
