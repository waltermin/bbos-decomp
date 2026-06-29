package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class AddressCollection$PostalCodeHandler implements ObjectFieldHandler {
   private AddressCollection$PostalCodeHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof MailingAddressModel) ? null : ((MailingAddressModel)item).getZipOrPostalCode();
   }

   AddressCollection$PostalCodeHandler(AddressCollection$1 x0) {
      this();
   }
}
