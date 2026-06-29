package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.text.PhoneTextFilter;

final class DTMFEchoFilter extends PhoneTextFilter {
   @Override
   public final boolean validate(char character) {
      if ('0' <= character && character <= '9') {
         return true;
      } else {
         return 'A' <= character && character <= 'Z' ? true : character == '#' || character == '*';
      }
   }
}
