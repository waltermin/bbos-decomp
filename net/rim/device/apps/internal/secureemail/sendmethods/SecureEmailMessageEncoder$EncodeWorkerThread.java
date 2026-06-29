package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SecureEmailMessageEncoder$EncodeWorkerThread extends PleaseWaitWorkerThread {
   private final SecureEmailMessageEncoder this$0;

   private SecureEmailMessageEncoder$EncodeWorkerThread(SecureEmailMessageEncoder _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void doWork() {
      AbortSendSecureEmailException e;
      try {
         try {
            this.this$0.doEncodeWork();
            return;
         } catch (AbortSendSecureEmailException var4) {
            e = var4;
         }
      } catch (Throwable var5) {
         this.setThrowable(e);
         return;
      }

      this.setThrowable(e);
   }

   SecureEmailMessageEncoder$EncodeWorkerThread(SecureEmailMessageEncoder x0, SecureEmailMessageEncoder$1 x1) {
      this(x0);
   }
}
