package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.TrackwheelListener;

final class IdleScreen$2 implements Runnable {
   private final TrackwheelListener val$trackwheelListener;
   private final int val$finalStatus;
   private final int val$finalTime;
   private final IdleScreen this$0;

   IdleScreen$2(IdleScreen _1, TrackwheelListener _2, int _3, int _4) {
      this.this$0 = _1;
      this.val$trackwheelListener = _2;
      this.val$finalStatus = _3;
      this.val$finalTime = _4;
   }

   @Override
   public final void run() {
      this.val$trackwheelListener.trackwheelClick(this.val$finalStatus, this.val$finalTime);
      this.this$0._busy = false;
   }
}
