package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.apps.api.framework.file.FileDialog;

final class ServiceConnection$6 implements Runnable {
   private final FileDialog val$fd;
   private final ServiceConnection this$0;

   ServiceConnection$6(ServiceConnection _1, FileDialog _2) {
      this.this$0 = _1;
      this.val$fd = _2;
   }

   @Override
   public final void run() {
      if (!this.this$0._stopped) {
         this.val$fd.doModal();
      }
   }
}
