package net.rim.device.apps.internal.lbs.model;

import net.rim.device.api.ui.text.TextFilter;

final class SearchAddressModel$AddressTextFilter extends TextFilter {
   @Override
   public final char convert(char character, int status) {
      return character == '+' ? '&' : character;
   }

   @Override
   public final boolean validate(char character) {
      return character >= 'a' && character <= 'z'
         || character >= 'A' && character <= 'Z'
         || character >= '0' && character <= '9'
         || character == ' '
         || character == '-'
         || character == '&'
         || character == '.'
         || character == ','
         || character == '\'';
   }
}
