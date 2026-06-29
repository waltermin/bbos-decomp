package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.ui.component.Dialog;

class SendDeleteListener$1 implements Runnable {
   private final Dialog val$dialog;
   private final SendDeleteListener this$0;

   SendDeleteListener$1(SendDeleteListener _1, Dialog _2) {
      this.this$0 = _1;
      this.val$dialog = _2;
   }

   @Override
   public void run() {
      this.val$dialog.show();
   }
}
