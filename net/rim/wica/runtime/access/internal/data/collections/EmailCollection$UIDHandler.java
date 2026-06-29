package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class EmailCollection$UIDHandler implements IntFieldHandler {
   private EmailCollection$UIDHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof EmailMessageModel) ? -1 : ((EmailMessageModel)item).getCMIMEReferenceIdentifier();
   }

   EmailCollection$UIDHandler(EmailCollection$1 x0) {
      this();
   }
}
