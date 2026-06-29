package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class EmailMoreVerb$EmailMoreRequestTransmissionWrapper$1 implements Runnable {
   private final EmailMoreVerb$EmailMoreRequestTransmissionWrapper this$0;

   EmailMoreVerb$EmailMoreRequestTransmissionWrapper$1(EmailMoreVerb$EmailMoreRequestTransmissionWrapper _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.alert(EmailResources.getString(138));
   }
}
