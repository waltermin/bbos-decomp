package net.rim.device.apps.internal.explorer.file.bluetooth;

final class ServiceConnection$4 implements Runnable {
   private final ServiceConnection this$0;

   ServiceConnection$4(ServiceConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._gaugePlaceholder != null) {
         this.this$0._vfm.replace(this.this$0._gaugePlaceholder, this.this$0._gaugeField);
         this.this$0._gaugePlaceholder = null;
      }
   }
}
