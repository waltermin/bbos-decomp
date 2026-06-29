package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.UiApplication;

class PopupStatus$PopScreenRunnable implements Runnable {
   private boolean _executed;
   private final PopupStatus this$0;

   PopupStatus$PopScreenRunnable(PopupStatus _1) {
      this.this$0 = _1;
   }

   public void init() {
      this._executed = false;
   }

   @Override
   public void run() {
      if (!this._executed) {
         UiApplication.getUiApplication().popScreen(this.this$0);
         this._executed = true;
      }
   }
}
