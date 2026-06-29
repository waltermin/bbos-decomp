package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EmailCollection$CCRecipientsHandler implements ObjectFieldHandler {
   private EmailCollection$CCRecipientsHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof EmailMessageModel) ? null : EmailCollection.getStringFieldValueFromModel((EmailMessageModel)item, 1, false);
   }

   EmailCollection$CCRecipientsHandler(EmailCollection$1 x0) {
      this();
   }
}
