package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.Transport;

public interface EmailTransport extends Transport {
   EmailMessageModel sendMessage(EmailMessageModel var1, ServiceRecord var2, Object var3);

   boolean canSendEmail(EmailMessageModel var1);

   EmailMessageModel resendMessage(EmailMessageModel var1, ServiceRecord var2, Object var3);

   boolean isBccSupported();

   boolean isCcSupported();

   boolean isAttachmentSupported(long var1);

   void saveMessage(EmailMessageModel var1, ServiceRecord var2, Object var3);
}
