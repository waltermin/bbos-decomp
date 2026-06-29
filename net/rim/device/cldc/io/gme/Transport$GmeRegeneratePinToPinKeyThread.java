package net.rim.device.cldc.io.gme;

import net.rim.device.api.system.PersistentContent;

final class Transport$GmeRegeneratePinToPinKeyThread extends Thread {
   private final Transport this$0;

   private Transport$GmeRegeneratePinToPinKeyThread(Transport _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Object ticket = PersistentContent.waitForTicket();
      if (ticket != null) {
         this.this$0.regeneratePinToPinKeyInfo();
         ticket = null;
      }
   }

   Transport$GmeRegeneratePinToPinKeyThread(Transport x0, Transport$1 x1) {
      this(x0);
   }
}
