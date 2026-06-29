package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class AddressCollection$ExtraHandler implements ObjectFieldHandler {
   private AddressCollection$ExtraHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : ((MailingAddressModel)item).getAddressLine2();
   }

   AddressCollection$ExtraHandler(AddressCollection$1 x0) {
      this();
   }
}
