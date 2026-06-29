package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$JobTitleHandler implements ObjectFieldHandler {
   private ContactCollection$JobTitleHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof Object) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof Object) {
               return ((TitleModel)subModel).getTitle();
            }
         }
      }

      return null;
   }

   ContactCollection$JobTitleHandler(ContactCollection$1 x0) {
      this();
   }
}
