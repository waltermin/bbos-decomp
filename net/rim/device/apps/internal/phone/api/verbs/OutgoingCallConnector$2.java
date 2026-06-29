package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Radio;
import net.rim.device.apps.api.ui.CommonResources;

class OutgoingCallConnector$2 implements Runnable {
   private final OutgoingCallConnector this$0;

   OutgoingCallConnector$2(OutgoingCallConnector _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Object o = null;
      if (this.this$0._communicationsState == 2) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         o = applicationRegistry.get(1185883946270450222L);
         if (!(o instanceof Runnable)) {
            Radio.requestPowerOn();
            System.out.println("OutgoingCallConnector requesting Radio on.");
         } else {
            Runnable r = (Runnable)o;
            OutgoingCallConnector.access$100(this.this$0).invokeLater(r);
            System.out.println("OutgoingCallConnector restoring connections.");
         }
      }

      this.this$0._waitForRadio = true;
      if (this.this$0._communicationsState != 2 && this.this$0._communicationsState != 4) {
         this.this$0.showStatus(CommonResources.getString(9156));
      } else if (o == null) {
         this.this$0.showStatus(CommonResources.getString(9155));
      } else {
         this.this$0.showStatus(CommonResources.getString(9176));
      }
   }
}
