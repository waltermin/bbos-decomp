package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.ui.component.Status;

final class ServiceProgramUI$StatusRunnable implements Runnable {
   String _message;
   boolean _exit;
   private final ServiceProgramUI this$0;

   ServiceProgramUI$StatusRunnable(ServiceProgramUI _1, String message, boolean exit) {
      this.this$0 = _1;
      this._message = message;
      this._exit = exit;
   }

   @Override
   public final void run() {
      Status.show(this._message);
      if (this._exit) {
         this.this$0.popScreen();
      }
   }
}
