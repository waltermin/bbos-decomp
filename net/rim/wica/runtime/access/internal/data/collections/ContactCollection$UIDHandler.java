package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class ContactCollection$UIDHandler implements IntFieldHandler {
   private ContactCollection$UIDHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof AddressCardModel) ? -1 : ((AddressCardModel)item).getUID();
   }

   ContactCollection$UIDHandler(ContactCollection$1 x0) {
      this();
   }
}
