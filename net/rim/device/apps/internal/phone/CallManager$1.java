package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CallManager$1 implements Runnable {
   private final int val$status;
   private final CallManager this$0;

   CallManager$1(CallManager _1, int _2) {
      this.this$0 = _1;
      this.val$status = _2;
   }

   @Override
   public final void run() {
      switch (this.val$status) {
         case 2:
         default:
            Dialog.inform(PhoneResources.getString(4));
            return;
         case 3:
            Dialog.alert(PhoneResources.getString(183));
            return;
         case 4:
            Dialog.alert(PhoneResources.getString(180));
         case 1:
      }
   }
}
