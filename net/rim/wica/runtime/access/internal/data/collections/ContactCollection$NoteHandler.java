package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$NoteHandler implements ObjectFieldHandler {
   private ContactCollection$NoteHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         AddressCardModel model = (AddressCardModel)item;
         int size = model.size();

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof BodyModel) {
               return ((BodyModel)subModel).getText();
            }
         }
      }

      return null;
   }

   ContactCollection$NoteHandler(ContactCollection$1 x0) {
      this();
   }
}
