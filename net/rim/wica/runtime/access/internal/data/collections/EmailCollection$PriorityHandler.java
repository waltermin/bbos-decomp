package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.wica.runtime.access.internal.data.enumeration.EmailPriorityEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class EmailCollection$PriorityHandler implements IntFieldHandler {
   private EmailCollection$PriorityHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof EmailMessageModel) ? -1 : EmailPriorityEnumConverter.deviceToCommon(((EmailMessageModel)item).getPriority());
   }

   EmailCollection$PriorityHandler(EmailCollection$1 x0) {
      this();
   }
}
