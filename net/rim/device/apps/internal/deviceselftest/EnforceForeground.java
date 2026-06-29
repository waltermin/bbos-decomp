package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.ApplicationManager;

final class EnforceForeground extends Thread {
   TestBacklight tb;

   EnforceForeground(TestBacklight t) {
      this.tb = t;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (this.tb.app.currentTest == this.tb) {
         if (ApplicationManager.getApplicationManager().getForegroundProcessId() != TestBacklight.processId) {
            ApplicationManager.getApplicationManager().requestForeground(TestBacklight.processId);
         }

         boolean var3 = false /* VF: Semaphore variable */;

         try {
            var3 = true;
            Thread.sleep(150);
            var3 = false;
         } finally {
            if (var3) {
               return;
            }
         }
      }
   }
}
