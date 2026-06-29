package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.component.Dialog;

final class ActivePhoneScreen$1 implements Runnable {
   private final Dialog val$dlg;
   private final ActivePhoneScreen this$0;

   ActivePhoneScreen$1(ActivePhoneScreen _1, Dialog _2) {
      this.this$0 = _1;
      this.val$dlg = _2;
   }

   @Override
   public final void run() {
      this.val$dlg.close();
   }
}
