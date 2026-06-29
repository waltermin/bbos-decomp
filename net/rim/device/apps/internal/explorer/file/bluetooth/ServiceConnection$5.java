package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.apps.api.framework.file.FileDialog;

final class ServiceConnection$5 implements Runnable {
   private final FileDialog val$fd;
   private final ServiceConnection this$0;

   ServiceConnection$5(ServiceConnection _1, FileDialog _2) {
      this.this$0 = _1;
      this.val$fd = _2;
   }

   @Override
   public final void run() {
      if (!this.this$0._stopped && !this.this$0._notifier.isConnected()) {
         this.this$0.stop();
         this.val$fd.cancel();
      }
   }
}
