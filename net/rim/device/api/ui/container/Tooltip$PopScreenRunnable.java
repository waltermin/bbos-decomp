package net.rim.device.api.ui.container;

import net.rim.device.api.ui.UiApplication;

class Tooltip$PopScreenRunnable implements Runnable {
   private boolean _executed;
   private int _invokeId;
   private final Tooltip this$0;

   Tooltip$PopScreenRunnable(Tooltip _1) {
      this.this$0 = _1;
      this._invokeId = -1;
   }

   public void init() {
      this._executed = false;
   }

   public void cancelInvokeLater() {
      if (this._invokeId != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._invokeId);
         this._invokeId = -1;
      }
   }

   @Override
   public void run() {
      this._invokeId = -1;
      if (!this._executed) {
         UiApplication.getUiApplication().popScreen(this.this$0._screen);
         this._executed = true;
      }
   }
}
