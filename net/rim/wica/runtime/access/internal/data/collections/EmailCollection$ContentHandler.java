package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EmailCollection$ContentHandler implements ObjectFieldHandler {
   private EmailCollection$ContentHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : ((EmailMessageModel)item).getBody();
   }

   EmailCollection$ContentHandler(EmailCollection$1 x0) {
      this();
   }
}
