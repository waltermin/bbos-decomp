package net.rim.device.apps.internal.addressbook.mailingaddress;

import net.rim.device.api.ui.text.UppercaseTextFilter;

class MailingAddressModelImpl$1 extends UppercaseTextFilter {
   private final MailingAddressModelImpl this$0;

   MailingAddressModelImpl$1(MailingAddressModelImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean validate(char character) {
      return character == '-' || character == ' ' || Character.isDigit(character) || super.validate(character);
   }
}
