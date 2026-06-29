package net.rim.device.apps.internal.qm.peer;

final class ConversationScreen$7 implements Runnable {
   private final Object val$element;
   private final ConversationScreen this$0;

   ConversationScreen$7(ConversationScreen _1, Object _2) {
      this.this$0 = _1;
      this.val$element = _2;
   }

   @Override
   public final void run() {
      ConversationScreen.access$200(this.this$0).remove((MessengerMessage)this.val$element);
   }
}
