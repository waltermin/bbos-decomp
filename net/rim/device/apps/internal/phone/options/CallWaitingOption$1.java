package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class CallWaitingOption$1 implements Runnable {
   private final CallWaitingOption this$0;

   CallWaitingOption$1(CallWaitingOption _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.alert(PhoneResources.getString(6296));
   }
}
