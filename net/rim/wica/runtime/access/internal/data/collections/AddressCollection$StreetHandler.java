package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class AddressCollection$StreetHandler implements ObjectFieldHandler {
   private AddressCollection$StreetHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof MailingAddressModel) ? null : ((MailingAddressModel)item).getAddressLine1();
   }

   AddressCollection$StreetHandler(AddressCollection$1 x0) {
      this();
   }
}
