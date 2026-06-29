package net.rim.device.apps.internal.messaging.search;

class MessageSearchImpl$1 implements Runnable {
   private final MessageSearchImpl this$0;

   MessageSearchImpl$1(MessageSearchImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (MessageSearchImpl.establishMMSSearch(this.this$0._searchCollection)) {
         this.this$0.refreshHotKeysFromCollection();
      }
   }
}
