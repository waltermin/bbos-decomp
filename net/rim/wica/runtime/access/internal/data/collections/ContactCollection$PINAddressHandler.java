package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$PINAddressHandler implements ObjectFieldHandler {
   private ContactCollection$PINAddressHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof PINAddressModel) {
               return ((PINAddressModel)subModel).getAddress();
            }
         }
      }

      return null;
   }

   ContactCollection$PINAddressHandler(ContactCollection$1 x0) {
      this();
   }
}
