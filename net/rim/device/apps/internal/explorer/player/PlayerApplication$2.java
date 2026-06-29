package net.rim.device.apps.internal.explorer.player;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;

final class PlayerApplication$2 extends Thread {
   private final Screen val$screen;
   private final PlayerApplication this$0;

   PlayerApplication$2(PlayerApplication _1, Screen _2) {
      this.this$0 = _1;
      this.val$screen = _2;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         if (this.this$0._renderScreen != null && this.val$screen == this.this$0._renderScreen) {
            this.this$0.popScreen(this.this$0._renderScreen);
         }

         this.this$0.stop();
      }
   }
}
