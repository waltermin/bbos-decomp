package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Screen;

final class CallDisplayDialog$1 implements Runnable {
   private final Screen val$screen;
   private final CallDisplayDialog this$0;

   CallDisplayDialog$1(CallDisplayDialog _1, Screen _2) {
      this.this$0 = _1;
      this.val$screen = _2;
   }

   @Override
   public final void run() {
      if (!this.this$0._isClosed) {
         this.this$0._app.queueStatus(this.val$screen, -2147483646, true);
      }
   }
}
