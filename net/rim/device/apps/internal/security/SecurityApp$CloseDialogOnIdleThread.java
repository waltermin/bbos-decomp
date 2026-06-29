package net.rim.device.apps.internal.security;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;

final class SecurityApp$CloseDialogOnIdleThread extends Thread {
   private UiApplication _uiApp;
   private SecurityApp _securityApp;
   private final SecurityApp this$0;

   SecurityApp$CloseDialogOnIdleThread(SecurityApp _1, SecurityApp securityApp) {
      this.this$0 = _1;
      this._securityApp = securityApp;
      this._uiApp = UiApplication.getUiApplication();
   }

   @Override
   public final void run() {
      while (this.this$0._promptDialog != null) {
         label67:
         try {
            Thread.sleep(1000);
         } finally {
            break label67;
         }

         if (DeviceInfo.getIdleTime() > 5) {
            SecurityApp$MyDialog dialog = this.this$0._promptDialog;
            if (dialog != null) {
               synchronized (this._securityApp) {
                  this._securityApp._closeThread = null;
               }

               synchronized (this._uiApp.getAppEventLock()) {
                  dialog.cancelDialog();
               }
            }
         }
      }

      synchronized (this._securityApp) {
         this._securityApp._closeThread = null;
      }
   }
}
