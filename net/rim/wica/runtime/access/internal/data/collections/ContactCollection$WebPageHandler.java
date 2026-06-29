package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.addressbook.addresscard.WebPageAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$WebPageHandler implements ObjectFieldHandler {
   private ContactCollection$WebPageHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof WebPageAddressModel) {
               return ((WebPageAddressModel)subModel).getAddress();
            }
         }
      }

      return null;
   }

   ContactCollection$WebPageHandler(ContactCollection$1 x0) {
      this();
   }
}
