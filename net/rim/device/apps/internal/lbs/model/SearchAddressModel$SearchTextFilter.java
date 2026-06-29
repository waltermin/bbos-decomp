package net.rim.device.apps.internal.lbs.model;

import net.rim.device.api.ui.text.TextFilter;

final class SearchAddressModel$SearchTextFilter extends TextFilter {
   @Override
   public final char convert(char character, int status) {
      return character;
   }

   @Override
   public final boolean validate(char character) {
      return character >= 'a' && character <= 'z'
         || character >= 'A' && character <= 'Z'
         || character == ' '
         || character == '.'
         || character == ','
         || character == '-'
         || character == '\'';
   }
}
