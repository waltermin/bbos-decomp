package net.rim.device.api.ui.text;

public class UppercaseTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      return Character.toUpperCase(character);
   }

   @Override
   public boolean validate(char character) {
      return Character.isUpperCase(character);
   }

   @Override
   public long getPreferredInputStyle() {
      return 1073741824;
   }
}
