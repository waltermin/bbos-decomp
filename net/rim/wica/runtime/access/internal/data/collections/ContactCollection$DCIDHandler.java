package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$DCIDHandler implements ObjectFieldHandler {
   private ContactCollection$DCIDHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel.getClass().getName().equals("net.rim.device.apps.internal.phone.direct.DirectConnectNumberModel")) {
               return ((AbstractPhoneNumberModel)subModel).getValue();
            }
         }
      }

      return null;
   }

   ContactCollection$DCIDHandler(ContactCollection$1 x0) {
      this();
   }
}
