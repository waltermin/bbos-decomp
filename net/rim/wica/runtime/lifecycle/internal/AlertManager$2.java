package net.rim.wica.runtime.lifecycle.internal;

import net.rim.device.api.notification.NotificationsManager;

class AlertManager$2 implements Runnable {
   private final AlertManager this$0;

   AlertManager$2(AlertManager this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      if (this.this$0._dialog == null) {
         NotificationsManager.cancelAllDeferredEvents(this.this$0._model.getId(), 1, null);
      } else {
         this.this$0._dialog.show();
      }
   }
}
