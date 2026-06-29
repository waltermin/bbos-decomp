package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$PhoneHandler implements ObjectFieldHandler {
   private int _phoneType;

   public ContactCollection$PhoneHandler(int phoneType) {
      this._phoneType = phoneType;
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof PhoneNumberModel && ((PhoneNumberModel)subModel).getType() == this._phoneType) {
               return ((PhoneNumberModel)subModel).getDisplayablePhoneNumber();
            }
         }
      }

      return null;
   }
}
