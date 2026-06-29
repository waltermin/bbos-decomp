package net.rim.device.internal.system;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;
import net.rim.device.internal.i18n.CommonResource;

class ForcedResetManager$ResetRunnable$DelayedResetRunnable implements Runnable {
   String _message;
   int _variant;
   private final ForcedResetManager$ResetRunnable this$0;

   ForcedResetManager$ResetRunnable$DelayedResetRunnable(ForcedResetManager$ResetRunnable _1, String msg, int variant) {
      this.this$0 = _1;
      this._message = msg;
      this._variant = variant;
   }

   @Override
   public void run() {
      new ForcedResetManager$ResetRunnable$DelayedResetRunnable$1(this).start();
      System.out.println("Showing status...");
      Status.show(CommonResource.getString(10101), Bitmap.getPredefinedBitmap(3), 2000, 33554432, false, true, -2147483647);
   }
}
