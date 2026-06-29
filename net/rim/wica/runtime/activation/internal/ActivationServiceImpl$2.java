package net.rim.wica.runtime.activation.internal;

import net.rim.blackberry.api.mail.SupportedAttachmentPart;

class ActivationServiceImpl$2 implements Runnable {
   private final SupportedAttachmentPart val$part;
   private final ActivationServiceImpl this$0;

   ActivationServiceImpl$2(ActivationServiceImpl this$0, SupportedAttachmentPart val$part) {
      this.this$0 = this$0;
      this.val$part = val$part;
   }

   @Override
   public void run() {
      this.this$0.invokeRegistration(this.val$part);
   }
}
