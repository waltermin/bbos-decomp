package net.rim.tid.itie;

import net.rim.device.api.ui.component.Status;

class IMContext$1 implements Runnable {
   private final IMContext this$0;

   IMContext$1(IMContext _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Status.show("Input system error. The system is being restarted.");
   }
}
