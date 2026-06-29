package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;

final class CallDisplayDialog$DismissDialogTimer implements Runnable {
   private boolean _executed;
   private Runnable _timeoutRunnable;
   private int _id;
   private Application _app;
   private final CallDisplayDialog this$0;

   public CallDisplayDialog$DismissDialogTimer(CallDisplayDialog _1, long delayMillis, Runnable timeoutRunnable) {
      this.this$0 = _1;
      this._executed = false;
      this._timeoutRunnable = timeoutRunnable;
      this._app = Application.getApplication();
      this._id = this._app.invokeLater(this, delayMillis, false);
      if (this._id == -1) {
         throw new Object("No timer available for status popup.");
      }
   }

   public final synchronized void cancel() {
      if (!this._executed) {
         this._executed = true;
         if (this._id != -1) {
            this._app.cancelInvokeLater(this._id);
         }
      }
   }

   @Override
   public final synchronized void run() {
      if (!this._executed) {
         this._app.invokeLater(new CallDisplayDialog$DismissDialogTimer$1(this));
         this._executed = true;
      }
   }
}
