package net.rim.device.apps.internal.explorer.file.bluetooth;

final class ServiceConnection$1 implements Runnable {
   private final ServiceConnection this$0;

   ServiceConnection$1(ServiceConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._cancelField.getManager() == this.this$0._vfm) {
         this.this$0._vfm.delete(this.this$0._cancelField);
      }

      if (this.this$0._notifier != null) {
         try {
            this.this$0._notifier.close();
         } finally {
            return;
         }
      }
   }
}
