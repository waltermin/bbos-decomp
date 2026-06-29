package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Message;

public class ApplicationStartupRunnable implements Runnable {
   private ApplicationDescriptor _application;

   public ApplicationStartupRunnable(ApplicationDescriptor application) {
      this._application = application;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void invokeLater() {
      int pid = -1;
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         pid = appManager.runApplication(this._application, false);
         var5 = false;
      } finally {
         if (var5) {
            return;
         }
      }

      Message invokeLaterMessage = (Message)(new Object(0, 2, this, null));
      ((ApplicationManagerInternal)appManager).postMessage(pid, invokeLaterMessage);
   }

   @Override
   public void run() {
      throw null;
   }
}
