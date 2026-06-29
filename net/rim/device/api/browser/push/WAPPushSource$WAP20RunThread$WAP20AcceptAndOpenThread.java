package net.rim.device.api.browser.push;

import net.rim.device.api.system.EventLogger;

final class WAPPushSource$WAP20RunThread$WAP20AcceptAndOpenThread extends Thread {
   private final WAPPushSource$WAP20RunThread this$1;

   WAPPushSource$WAP20RunThread$WAP20AcceptAndOpenThread(WAPPushSource$WAP20RunThread _1) {
      this.this$1 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         this.this$1.doAcceptAndOpen();
      } catch (Throwable var3) {
         if (!this.this$1.this$0._processing) {
            return;
         }

         EventLogger.logEvent(-1133226195824034738L, ((StringBuffer)(new Object("PTex\n"))).append(t.toString()).toString().getBytes(), 0);
         return;
      }
   }
}
