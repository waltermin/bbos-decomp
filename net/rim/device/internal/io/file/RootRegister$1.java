package net.rim.device.internal.io.file;

class RootRegister$1 implements Runnable {
   private final RootRegister this$0;

   RootRegister$1(RootRegister _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.mountOrUSBEnable();
   }
}
