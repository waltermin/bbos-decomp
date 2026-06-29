package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.component.Dialog;

class DialVerb$1 implements Runnable {
   private final Dialog val$askDialog;
   private final DialVerb this$0;

   DialVerb$1(DialVerb _1, Dialog _2) {
      this.this$0 = _1;
      this.val$askDialog = _2;
   }

   @Override
   public void run() {
      this.val$askDialog.doModal();
   }
}
