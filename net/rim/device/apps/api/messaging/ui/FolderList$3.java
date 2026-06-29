package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.collection.Collection;

class FolderList$3 implements Runnable {
   private final Collection val$collection;
   private final Object val$oldElement;
   private final Object val$newElement;
   private final FolderList this$0;

   FolderList$3(FolderList _1, Collection _2, Object _3, Object _4) {
      this.this$0 = _1;
      this.val$collection = _2;
      this.val$oldElement = _3;
      this.val$newElement = _4;
   }

   @Override
   public void run() {
      this.this$0.handleElementUpdated(this.val$collection, this.val$oldElement, this.val$newElement);
   }
}
