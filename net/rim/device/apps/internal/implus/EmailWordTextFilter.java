package net.rim.device.apps.internal.implus;

import net.rim.device.api.ui.text.EmailAddressTextFilter;

final class EmailWordTextFilter extends EmailAddressTextFilter {
   public EmailWordTextFilter() {
   }

   @Override
   public final boolean validate(char character) {
      switch (character) {
         case ' ':
         case '.':
         case '@':
            return false;
         default:
            return super.validate(character);
      }
   }
}
