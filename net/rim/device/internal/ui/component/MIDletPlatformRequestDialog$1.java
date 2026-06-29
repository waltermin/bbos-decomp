package net.rim.device.internal.ui.component;

class MIDletPlatformRequestDialog$1 implements Runnable {
   private final MIDletPlatformRequestDialog this$0;

   MIDletPlatformRequestDialog$1(MIDletPlatformRequestDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.close(-1);
   }
}
