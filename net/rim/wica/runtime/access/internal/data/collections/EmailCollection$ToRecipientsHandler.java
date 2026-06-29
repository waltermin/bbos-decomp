package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EmailCollection$ToRecipientsHandler implements ObjectFieldHandler {
   private EmailCollection$ToRecipientsHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : EmailCollection.getStringFieldValueFromModel((EmailMessageModel)item, 0, false);
   }

   EmailCollection$ToRecipientsHandler(EmailCollection$1 x0) {
      this();
   }
}
