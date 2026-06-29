package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.collection.Collection;

class FolderList$2 implements Runnable {
   private final Collection val$collection;
   private final Object val$element;
   private final FolderList this$0;

   FolderList$2(FolderList _1, Collection _2, Object _3) {
      this.this$0 = _1;
      this.val$collection = _2;
      this.val$element = _3;
   }

   @Override
   public void run() {
      this.this$0.handleElementAdded(this.val$collection, this.val$element);
   }
}
