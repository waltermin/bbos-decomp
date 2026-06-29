package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;

final class ConversationScreen$2 implements Runnable {
   private final Object val$context;
   private final ConversationScreen this$0;

   ConversationScreen$2(ConversationScreen _1, Object _2) {
      this.this$0 = _1;
      this.val$context = _2;
   }

   @Override
   public final void run() {
      if ((!(this.val$context instanceof ContextObject) || !((ContextObject)this.val$context).getFlag(26))
         && ConversationScreen.access$100(this.this$0) != null) {
         ConversationScreen.access$100(this.this$0).setFocus();
      } else {
         ConversationScreen.access$200(this.this$0).setFocus();
         Field focusField = ConversationScreen.access$200(this.this$0).scroll(142);
         if (focusField != null) {
            focusField.setFocus();
         }

         if (ConversationScreen.access$300(this.this$0) == null && this.val$context instanceof ContextObject) {
            ConversationScreen.access$302(this.this$0, (ContextObject)this.val$context);
            return;
         }
      }
   }
}
