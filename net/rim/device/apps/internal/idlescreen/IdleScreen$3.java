package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.TrackwheelListener;

final class IdleScreen$3 implements Runnable {
   private final TrackwheelListener val$trackwheelListener;
   private final int val$finalAmount;
   private final int val$finalStatus;
   private final int val$finalTime;
   private final IdleScreen this$0;

   IdleScreen$3(IdleScreen _1, TrackwheelListener _2, int _3, int _4, int _5) {
      this.this$0 = _1;
      this.val$trackwheelListener = _2;
      this.val$finalAmount = _3;
      this.val$finalStatus = _4;
      this.val$finalTime = _5;
   }

   @Override
   public final void run() {
      this.val$trackwheelListener.trackwheelRoll(this.val$finalAmount, this.val$finalStatus, this.val$finalTime);
      this.this$0._busy = false;
   }
}
