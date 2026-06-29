package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.system.EventLogger;

final class DownloadDescriptorStatusReport$1 extends Thread {
   private final DownloadDescriptorStatusReport this$0;

   DownloadDescriptorStatusReport$1(DownloadDescriptorStatusReport _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      boolean responseTimedOut = false;
      synchronized (this.this$0._waitingForResponseLock) {
         if (this.this$0._fetchRequest != null) {
            label91:
            try {
               this.this$0._waitingForResponseLock.wait(5000);
            } finally {
               break label91;
            }

            if (this.this$0._fetchRequest != null) {
               responseTimedOut = true;
            }
         }
      }

      if (responseTimedOut) {
         if (this.this$0._listener != null) {
            EventLogger.logEvent(1907089860548946979L, 1145335412);
            this.this$0._listener.finishedStatusReport(200);
         }

         try {
            this.this$0._fetchRequest.abort();
         } finally {
            return;
         }
      }
   }
}
