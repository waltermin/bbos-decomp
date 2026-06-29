package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class CallForwardingOption$1 implements Runnable {
   private final CallForwardingOption this$0;

   CallForwardingOption$1(CallForwardingOption _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.alert(PhoneResources.getString(6052));
   }
}
