package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.text.TextFilter;

final class CategoryEditField$CategoryTextFilter extends TextFilter {
   @Override
   public final char convert(char character, int status) {
      return character;
   }

   @Override
   public final boolean validate(char character) {
      switch (character) {
         case ',':
         case ';':
            return false;
         default:
            return true;
      }
   }
}
