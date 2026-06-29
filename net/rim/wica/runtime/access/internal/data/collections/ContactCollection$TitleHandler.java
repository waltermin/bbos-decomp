package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$TitleHandler implements ObjectFieldHandler {
   private ContactCollection$TitleHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         PersonNameModel name = ((AddressCardModel)item).getName();
         if (name != null) {
            return name.getSalutation();
         }
      }

      return null;
   }

   ContactCollection$TitleHandler(ContactCollection$1 x0) {
      this();
   }
}
