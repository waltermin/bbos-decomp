package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MergedCollection;
import net.rim.device.apps.internal.ribbon.skin.svg.NewMessageFilter;

class EmailHandlerFactory$1 implements Runnable {
   private final EmailHandlerFactory this$0;

   EmailHandlerFactory$1(EmailHandlerFactory _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      ReadableList fullList = (MergedCollection)FolderMerge.getMergeCollection(-5581791943352753293L);
      this.this$0._collection = new NewMessageFilter(fullList);
      ((CollectionEventSource)fullList).addCollectionListener(this.this$0._collection);
      this.this$0._collection.addCollectionListener(this.this$0._collectionEventHelper);
      synchronized (this.this$0._collectionEventHelper) {
         this.this$0._initialized = true;
         if (this.this$0._handlerForUpdate != null) {
            this.this$0._handlerForUpdate._collection = this.this$0._collection;
            this.this$0._handlerForUpdate.update(false);
            this.this$0._handlerForUpdate = null;
         }
      }
   }
}
