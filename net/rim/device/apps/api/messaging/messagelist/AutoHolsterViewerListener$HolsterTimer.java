package net.rim.device.apps.api.messaging.messagelist;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

final class AutoHolsterViewerListener$HolsterTimer {
   private Timer _timer;
   private final AutoHolsterViewerListener this$0;
   public static final long SAFETY_OFFSET;

   private AutoHolsterViewerListener$HolsterTimer(AutoHolsterViewerListener _1) {
      this.this$0 = _1;
   }

   public final void startTimer(long timelimit) {
      this._timer = (Timer)(new Object());
      this._timer.schedule(this.getNewTimerTaskInstance(), (Date)(new Object(timelimit - 1000)));
   }

   public final void resetTimer(long timelimit) {
      this.cancelTimer();
      this.startTimer(timelimit);
   }

   public final void cancelTimer() {
      this._timer.cancel();
   }

   public final TimerTask getNewTimerTaskInstance() {
      return new AutoHolsterViewerListener$HolsterTimer$1(this);
   }

   AutoHolsterViewerListener$HolsterTimer(AutoHolsterViewerListener x0, AutoHolsterViewerListener$1 x1) {
      this(x0);
   }
}
