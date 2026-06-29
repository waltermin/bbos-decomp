package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.UiApplication;

final class ConversationScreen$4 implements Runnable {
   private final Object val$message;
   private final ConversationScreen this$0;

   ConversationScreen$4(ConversationScreen _1, Object _2) {
      this.this$0 = _1;
      this.val$message = _2;
   }

   @Override
   public final void run() {
      if (UiApplication.getUiApplication() == ConversationScreen.access$400(this.this$0)) {
         ConversationScreen.access$000(this.this$0, this.val$message);
      } else {
         this.this$0.updateNotification(this.val$message);
      }
   }
}
