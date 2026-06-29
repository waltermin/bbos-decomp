package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.system.Application;

final class FolderList$Updater implements Runnable {
   private final FolderList this$0;

   public FolderList$Updater(FolderList _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (Application.getApplication().getAppEventLock()) {
         synchronized (this.this$0._lockObj) {
            this.this$0.clearTree();
            this.this$0.constructTree();
            this.this$0.updateFocus();
         }
      }
   }
}
