package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.model.RIMModel;

class MessageListUI$1 implements Runnable {
   private final RIMModel val$modelToOpen;
   private final MessageListUI this$0;

   MessageListUI$1(MessageListUI _1, RIMModel _2) {
      this.this$0 = _1;
      this.val$modelToOpen = _2;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         this.this$0._selectedIndexManager.setSelectedIndex(this.this$0._sortedSeparatedItems.getIndex(this.val$modelToOpen));
      }
   }
}
