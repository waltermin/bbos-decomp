package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class AddressCollection$CountryHandler implements ObjectFieldHandler {
   private AddressCollection$CountryHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : ((MailingAddressModel)item).getCountry();
   }

   AddressCollection$CountryHandler(AddressCollection$1 x0) {
      this();
   }
}
