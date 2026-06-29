package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$GivenNameHandler implements ObjectFieldHandler {
   private ContactCollection$GivenNameHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof Object) {
         PersonNameModel name = ((AddressCardModel)item).getName();
         if (name != null) {
            return name.getFirstName();
         }
      }

      return null;
   }

   ContactCollection$GivenNameHandler(ContactCollection$1 x0) {
      this();
   }
}
