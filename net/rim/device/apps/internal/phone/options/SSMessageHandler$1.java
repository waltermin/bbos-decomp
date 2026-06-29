package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class SSMessageHandler$1 implements Runnable {
   private final int val$type;
   private final SSMessageHandler this$0;

   SSMessageHandler$1(SSMessageHandler _1, int _2) {
      this.this$0 = _1;
      this.val$type = _2;
   }

   @Override
   public void run() {
      String prompt;
      switch (this.val$type) {
         case 1:
         default:
            prompt = CommonResources.getString(2012);
            break;
         case 2:
            prompt = PhoneResources.getString(6290);
            break;
         case 3:
            prompt = CommonResources.getString(2011);
      }

      String password = SSManager.getCallBarringPasswordFromUser(prompt);
      if (password != null) {
         try {
            Phone.getInstance().sendSSPasswordResponse(password);
         } finally {
            return;
         }
      }
   }
}
