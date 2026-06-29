package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.addressbook.userfields.UserFieldsModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$UserFieldHandler implements ObjectFieldHandler {
   private int _userIndex;
   private boolean _valid;
   private static final int NUM_USER_FIELDS;

   public ContactCollection$UserFieldHandler(int userIndex) {
      this._userIndex = userIndex;
      if (this._userIndex >= 0 && this._userIndex < 4) {
         this._valid = true;
      }
   }

   @Override
   public final Object getValue(Object item) {
      if (this._valid && item instanceof Object) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof Object) {
               return ((UserFieldsModel)subModel).getUserDefinedField(this._userIndex);
            }
         }
      }

      return null;
   }
}
