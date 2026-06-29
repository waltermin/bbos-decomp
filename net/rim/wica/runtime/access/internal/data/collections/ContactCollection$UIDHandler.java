package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class ContactCollection$UIDHandler implements IntFieldHandler {
   private ContactCollection$UIDHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof Object) ? -1 : ((AddressCardElement)item).getUID();
   }

   ContactCollection$UIDHandler(ContactCollection$1 x0) {
      this();
   }
}
