package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

class PhoneUtilities$1 extends Thread {
   private final Runnable val$_runnable;
   private final Dialog val$status;

   PhoneUtilities$1(Runnable _1, Dialog _2) {
      this.val$_runnable = _1;
      this.val$status = _2;
   }

   @Override
   public void run() {
      long time = System.currentTimeMillis();
      this.val$_runnable.run();
      time = System.currentTimeMillis() - time;
      if (time < 1500) {
         label30:
         try {
            Thread.sleep(1500);
         } finally {
            break label30;
         }
      }

      synchronized (Application.getEventLock()) {
         this.val$status.close();
      }
   }
}
