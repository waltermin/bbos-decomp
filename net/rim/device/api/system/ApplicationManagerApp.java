package net.rim.device.api.system;

import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Process;

final class ApplicationManagerApp {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void main(String[] args) {
      Process.currentProcess();
      String oom = "Application Manager threw Out of Memory";

      try {
         new Object();
      } catch (Throwable var9) {
         Throwable ex = var9;

         try {
            String msg = ex.getMessage();
            if (msg == null) {
               msg = ex.toString();
            }

            InternalServices.catastrophicFailure(200, ((StringBuffer)(new Object("Application Manager threw "))).append(msg).toString());
            return;
         } finally {
            InternalServices.catastrophicFailure(200, oom);
            return;
         }
      }
   }
}
