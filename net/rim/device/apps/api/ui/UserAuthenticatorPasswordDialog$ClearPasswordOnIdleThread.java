package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;

class UserAuthenticatorPasswordDialog$ClearPasswordOnIdleThread extends Thread {
   private Application _app;
   private boolean _stopThread;
   private final UserAuthenticatorPasswordDialog this$0;

   UserAuthenticatorPasswordDialog$ClearPasswordOnIdleThread(UserAuthenticatorPasswordDialog _1, Application app) {
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
         boolean var7 = false /* VF: Semaphore variable */;

         label85: {
            try {
               var7 = true;
               if (!this._stopThread) {
                  label77:
                  try {
                     Thread.sleep(1000);
                  } finally {
                     break label77;
                  }

                  if (DeviceInfo.getIdleTime() <= 10 || DeviceInfo.isSimulator()) {
                     continue;
                  }

                  synchronized (this._app.getAppEventLock()) {
                     this.this$0._devicePasswordField.setText("");
                     this.this$0._authenticatorPasswordField.setText("");
                     this.this$0.passwordEntryStateChanged();
                     var7 = false;
                     break label85;
                  }
               }

               var7 = false;
            } finally {
               if (var7) {
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
