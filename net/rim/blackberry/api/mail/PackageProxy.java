package net.rim.blackberry.api.mail;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

public final class PackageProxy {
   private PackageProxy() {
   }

   public static final Message createMessageFromInternalModel(EmailMessageModel emm) {
      return new Message(emm);
   }

   public static final void libMain(String[] args) {
      UnsupportedMailConverter.register();
      TextMailConverter.register();
      SupportedAttachmentConverter.register();
      ListenerManager.register();
   }
}
