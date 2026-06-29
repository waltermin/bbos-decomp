package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.component.BasicEditField;

class SimplePasswordDialog$ClearPasswordOnIdleThread extends Thread {
   private Application _app;
   private boolean _stopThread;
   private final SimplePasswordDialog this$0;

   SimplePasswordDialog$ClearPasswordOnIdleThread(SimplePasswordDialog _1, Application app) {
      this.this$0 = _1;
      this._app = app;
   }

   public void stopThread() {
      this._stopThread = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      while (true) {
         boolean var8 = false /* VF: Semaphore variable */;

         label63: {
            try {
               var8 = true;
               if (!this._stopThread) {
                  try {
                     Thread.sleep(1000);
                  } catch (InterruptedException var10) {
                  }

                  BasicEditField editField = this.this$0.getEditField();
                  if (editField == null || DeviceInfo.getIdleTime() <= 10 || DeviceInfo.isSimulator()) {
                     continue;
                  }

                  synchronized (this._app.getAppEventLock()) {
                     editField.setText("");
                     this.this$0.passwordEntryStateChanged();
                     var8 = false;
                     break label63;
                  }
               }

               var8 = false;
            } finally {
               if (var8) {
                  this.this$0._clearPasswordOnIdleThread = null;
               }
            }

            this.this$0._clearPasswordOnIdleThread = null;
            return;
         }

         this.this$0._clearPasswordOnIdleThread = null;
         return;
      }
   }
}
