package net.rim.wica.runtime.activation.internal;

import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.blackberry.api.mail.Transport;

class ActivationServiceImpl$1 implements Runnable {
   private final SupportedAttachmentPart val$part;
   private final ActivationServiceImpl this$0;

   ActivationServiceImpl$1(ActivationServiceImpl this$0, SupportedAttachmentPart val$part) {
      this.this$0 = this$0;
      this.val$part = val$part;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         Transport.more(this.val$part, true);
         long threadSleepTime = 2000;
         long expiryThreshold = 60000;
         long totalThreadSleepTime = 0;
         boolean expired = false;
         synchronized (this) {
            while (this.val$part.hasMore() && !expired) {
               label85:
               try {
                  this.wait(threadSleepTime);
               } finally {
                  break label85;
               }

               totalThreadSleepTime += threadSleepTime;
               expired = totalThreadSleepTime >= expiryThreshold;
            }
         }

         if (!this.val$part.hasMore() && !expired) {
            this.this$0.invokeRegistration(this.val$part);
            return;
         }
      } catch (Throwable var19) {
         e.printStackTrace();
         return;
      }
   }
}
