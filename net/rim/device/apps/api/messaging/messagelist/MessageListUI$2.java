package net.rim.device.apps.api.messaging.messagelist;

class MessageListUI$2 implements Runnable {
   private final ShowMessageAppVerb val$verbToInvoke;
   private final Object val$contextParameter;
   private final MessageListUI this$0;

   MessageListUI$2(MessageListUI _1, ShowMessageAppVerb _2, Object _3) {
      this.this$0 = _1;
      this.val$verbToInvoke = _2;
      this.val$contextParameter = _3;
   }

   @Override
   public void run() {
      this.this$0._creationApp.requestForeground();
      this.val$verbToInvoke.doInvoke(this.val$contextParameter);
   }
}
