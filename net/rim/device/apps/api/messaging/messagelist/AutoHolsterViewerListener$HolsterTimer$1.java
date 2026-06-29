package net.rim.device.apps.api.messaging.messagelist;

import java.util.TimerTask;

class AutoHolsterViewerListener$HolsterTimer$1 extends TimerTask {
   private final AutoHolsterViewerListener$HolsterTimer this$1;

   AutoHolsterViewerListener$HolsterTimer$1(AutoHolsterViewerListener$HolsterTimer _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      if (this.this$1.this$0._holstered) {
         this.this$1.this$0.terminateOperationInternal();
      }
   }
}
