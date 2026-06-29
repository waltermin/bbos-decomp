package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.notification.NotificationsManager;

class Channels$1$1 implements Runnable {
   private final Channels$1 this$0;

   Channels$1$1(Channels$1 _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      int oldStatus = this.this$0.val$channel.getStatus();
      ChannelModel.changeStatus(this.this$0.val$channel, 0);
      if (oldStatus != 0 && this.this$0.val$channel.getPriority() == 3) {
         NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
      }
   }
}
