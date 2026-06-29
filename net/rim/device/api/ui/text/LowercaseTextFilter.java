package net.rim.device.api.ui.text;

public class LowercaseTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      return Character.toLowerCase(character);
   }

   @Override
   public boolean validate(char character) {
      return Character.isLowerCase(character);
   }
}
