package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EmailCollection$ReplyToHandler implements ObjectFieldHandler {
   private EmailCollection$ReplyToHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : EmailCollection.getStringFieldValueFromModel((EmailMessageModel)item, 5, false);
   }

   EmailCollection$ReplyToHandler(EmailCollection$1 x0) {
      this();
   }
}
