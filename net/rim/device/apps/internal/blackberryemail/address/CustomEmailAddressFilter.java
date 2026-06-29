package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.ui.text.EmailAddressTextFilter;

final class CustomEmailAddressFilter extends EmailAddressTextFilter {
   public CustomEmailAddressFilter() {
   }

   @Override
   public final boolean validate(char character) {
      switch (character) {
         case '[':
         case ']':
            return true;
         default:
            return super.validate(character);
      }
   }
}
