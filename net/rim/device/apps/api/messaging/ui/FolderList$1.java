package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.collection.Collection;

class FolderList$1 implements Runnable {
   private final Collection val$collection;
   private final FolderList this$0;

   FolderList$1(FolderList _1, Collection _2) {
      this.this$0 = _1;
      this.val$collection = _2;
   }

   @Override
   public void run() {
      this.this$0.handleReset(this.val$collection);
   }
}
