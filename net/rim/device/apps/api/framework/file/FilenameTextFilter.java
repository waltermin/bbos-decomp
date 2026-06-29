package net.rim.device.apps.api.framework.file;

import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.io.FilenameValidator;

public class FilenameTextFilter extends TextFilter {
   @Override
   public char convert(char character, int status) {
      if (FilenameValidator.validateChar(character)) {
         return character;
      }

      character = CharacterUtilities.getOriginal(character);
      return FilenameValidator.validateChar(character) ? character : '_';
   }

   @Override
   public boolean validate(char character) {
      return FilenameValidator.validateChar(character);
   }

   public static boolean staticValidate(char character) {
      return FilenameValidator.validateChar(character);
   }
}
