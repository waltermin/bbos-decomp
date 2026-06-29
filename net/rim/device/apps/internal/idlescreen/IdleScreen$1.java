package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.StylusListener;

final class IdleScreen$1 implements Runnable {
   private final StylusListener val$stylusListener;
   private final int val$finalX;
   private final int val$finalY;
   private final int val$finalStatus;
   private final int val$finalTime;
   private final IdleScreen this$0;

   IdleScreen$1(IdleScreen _1, StylusListener _2, int _3, int _4, int _5, int _6) {
      this.this$0 = _1;
      this.val$stylusListener = _2;
      this.val$finalX = _3;
      this.val$finalY = _4;
      this.val$finalStatus = _5;
      this.val$finalTime = _6;
   }

   @Override
   public final void run() {
      this.val$stylusListener.stylusTap(this.val$finalX, this.val$finalY, this.val$finalStatus, this.val$finalTime);
      this.this$0._busy = false;
   }
}
