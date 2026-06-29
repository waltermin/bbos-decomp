package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class SSRequest$3 implements Runnable {
   private final SSRequest this$0;

   SSRequest$3(SSRequest _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.alert(PhoneResources.getString(467));
   }
}
