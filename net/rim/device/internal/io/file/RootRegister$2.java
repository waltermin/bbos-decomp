package net.rim.device.internal.io.file;

import net.rim.device.internal.system.USBPortInternal;

class RootRegister$2 implements Runnable {
   private final RootRegister this$0;

   RootRegister$2(RootRegister _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if ((USBPortInternal.getConnectionState() & 4) != 0) {
         this.this$0.dismissUsbMSDialogs();
         this.this$0._asked = false;
         this.this$0.disableUSBMassStorage();
         this.this$0.mountSDCard();
      }
   }
}
