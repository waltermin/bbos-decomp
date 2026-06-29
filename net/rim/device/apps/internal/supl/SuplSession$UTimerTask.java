package net.rim.device.apps.internal.supl;

import java.util.TimerTask;

final class SuplSession$UTimerTask extends TimerTask {
   private int timerId;
   private final SuplSession this$0;
   static final int CONNECTION_EST_GUARD_TIMER = 0;
   static final int UT1_TIMER = 1;
   static final int UT2_TIMER = 2;
   static final int UT3_TIMER = 3;

   SuplSession$UTimerTask(SuplSession _1, int id) {
      this.this$0 = _1;
      this.timerId = id;
   }

   @Override
   public final void run() {
      this.this$0.timerExpired(this.timerId);
   }
}
