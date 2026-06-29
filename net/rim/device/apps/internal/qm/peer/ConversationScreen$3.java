package net.rim.device.apps.internal.qm.peer;

final class ConversationScreen$3 implements Runnable {
   private final ConversationScreen this$0;

   ConversationScreen$3(ConversationScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0.isDisplayed()) {
         this.this$0.close();
      }
   }
}
