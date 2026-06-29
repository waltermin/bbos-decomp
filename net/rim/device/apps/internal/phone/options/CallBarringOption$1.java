package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class CallBarringOption$1 implements Runnable {
   private final CallBarringOption this$0;

   CallBarringOption$1(CallBarringOption _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.alert(PhoneResources.getString(6053));
   }
}
