package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$EmailHandler implements ObjectFieldHandler {
   private int _emailIndex;

   public ContactCollection$EmailHandler(int emailIndex) {
      this._emailIndex = emailIndex;
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof Object) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();
         int emailCount = 0;

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof Object) {
               if (emailCount == this._emailIndex) {
                  return ((FriendlyNameAddressModel)subModel).getAddress();
               }

               emailCount++;
            }
         }
      }

      return null;
   }
}
