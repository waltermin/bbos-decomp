package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.MMS;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MergedCollection;
import net.rim.device.apps.internal.ribbon.skin.svg.NewMessageFilter;

class SMSHandlerFactory$1 implements Runnable {
   private final SMSHandlerFactory this$0;

   SMSHandlerFactory$1(SMSHandlerFactory _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      ReadableList fullList = (MergedCollection)FolderMerge.getMergeCollection(-4696470826620059293L);
      this.this$0._collection = new NewMessageFilter(fullList);
      ((CollectionEventSource)fullList).addCollectionListener(this.this$0._collection);
      this.this$0._collection.addCollectionListener(this.this$0._helper);
      synchronized (this.this$0._helper) {
         this.this$0._initialized = true;
         if (this.this$0._handlerForUpdate != null) {
            this.this$0._handlerForUpdate._collection = this.this$0._collection;
            this.this$0._handlerForUpdate.update(false);
            MMS.onEnabled(this.this$0._handlerForUpdate);
            this.this$0._handlerForUpdate = null;
         }
      }
   }
}
