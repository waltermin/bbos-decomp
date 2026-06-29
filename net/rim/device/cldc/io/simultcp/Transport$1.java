package net.rim.device.cldc.io.simultcp;

import net.rim.device.api.ui.component.Dialog;

class Transport$1 implements Runnable {
   private final Transport this$0;

   Transport$1(Transport _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.inform("API versions between the simulator DLL and this Java build do not match.  Some apps might not work.");
   }
}
