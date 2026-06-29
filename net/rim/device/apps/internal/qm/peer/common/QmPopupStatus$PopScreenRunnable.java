package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.UiApplication;

final class QmPopupStatus$PopScreenRunnable implements Runnable {
   private boolean _executed;
   private final QmPopupStatus this$0;

   QmPopupStatus$PopScreenRunnable(QmPopupStatus _1) {
      this.this$0 = _1;
   }

   public final void init() {
      this._executed = false;
   }

   @Override
   public final void run() {
      if (!this._executed) {
         UiApplication.getUiApplication().popScreen(this.this$0);
         this._executed = true;
      }
   }
}
