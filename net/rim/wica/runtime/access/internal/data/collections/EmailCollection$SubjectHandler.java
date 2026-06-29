package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EmailCollection$SubjectHandler implements ObjectFieldHandler {
   private EmailCollection$SubjectHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof EmailMessageModel) ? null : ((EmailMessageModel)item).getSubject();
   }

   EmailCollection$SubjectHandler(EmailCollection$1 x0) {
      this();
   }
}
