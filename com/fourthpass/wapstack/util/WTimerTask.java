package com.fourthpass.wapstack.util;

import java.util.TimerTask;

final class WTimerTask extends TimerTask {
   ITimerScheduler _scheduler;
   byte _schedulerId;
   private WTimer _timer;

   WTimerTask(WTimer timer, ITimerScheduler scheduler, byte id) {
      this._timer = timer;
      this._scheduler = scheduler;
      this._schedulerId = id;
   }

   @Override
   public final void run() {
      this._timer.removeTask(this._scheduler, this._schedulerId);
      this._scheduler.timerExpired(this._schedulerId);
   }
}
