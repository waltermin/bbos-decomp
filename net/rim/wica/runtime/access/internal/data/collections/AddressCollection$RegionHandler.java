package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class AddressCollection$RegionHandler implements ObjectFieldHandler {
   private AddressCollection$RegionHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : ((MailingAddressModel)item).getArea();
   }

   AddressCollection$RegionHandler(AddressCollection$1 x0) {
      this();
   }
}
