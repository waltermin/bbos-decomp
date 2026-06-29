package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EmailCollection$BCCRecipientsHandler implements ObjectFieldHandler {
   private EmailCollection$BCCRecipientsHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof EmailMessageModel) ? null : EmailCollection.getStringFieldValueFromModel((EmailMessageModel)item, 2, false);
   }

   EmailCollection$BCCRecipientsHandler(EmailCollection$1 x0) {
      this();
   }
}
