package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.component.Dialog;

final class ActivePhoneScreen$ShowHomeScreenVerb$1 implements Runnable {
   private final Dialog val$dlg;
   private final ActivePhoneScreen$ShowHomeScreenVerb this$1;

   ActivePhoneScreen$ShowHomeScreenVerb$1(ActivePhoneScreen$ShowHomeScreenVerb _1, Dialog _2) {
      this.this$1 = _1;
      this.val$dlg = _2;
   }

   @Override
   public final void run() {
      this.val$dlg.close();
   }
}
