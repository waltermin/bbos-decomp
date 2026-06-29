package net.rim.wica.runtime.management.internal;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.logging.Logger;

final class ManagementServiceImpl$InCoverageTimer implements EventListener {
   private final int[] events;
   private ManagementServiceImpl$CancelRegTimerTask _task;
   private boolean _inCoverage;
   private long _timeCreated;
   private long _timeLeft;
   private final ManagementServiceImpl this$0;
   private static final long WAIT_FOR_POLICIES_DELAY;

   public ManagementServiceImpl$InCoverageTimer(ManagementServiceImpl this$0) {
      this(this$0, 600000);
   }

   public ManagementServiceImpl$InCoverageTimer(ManagementServiceImpl this$0, long delay) {
      this.this$0 = this$0;
      this.events = new int[]{300, 302, 51, -804651000, 100, 107, 109, 110};
      Logger.log("M AP-W");
      this._inCoverage = this$0._commService.isInCoverage();
      synchronized (this) {
         if (this._inCoverage) {
            this._timeLeft = delay;
            this.createTask();
         }
      }
   }

   private final void createTask() {
      if (this.this$0._timer != null && this._timeLeft > 0) {
         this._task = new ManagementServiceImpl$CancelRegTimerTask(this.this$0, null);
         if (this._task != null) {
            this.this$0._eventService.addListener(this.events, this);
            this._timeCreated = System.currentTimeMillis();
            this.this$0._timer.schedule(this._task, this._timeLeft);
         }
      }
   }

   public final void cleanup() {
      synchronized (this) {
         this.this$0._eventService.removeListener(this.events, this);
         if (this._task != null) {
            this._task.cancel();
            this._task = null;
         }
      }
   }

   public final long getTimeLeft() {
      return this._timeLeft;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 300:
            this.cleanup();
            this._timeLeft = this._timeLeft - (System.currentTimeMillis() - this._timeCreated);
            return;
         case 302:
            synchronized (this) {
               if (this._task == null) {
                  this.createTask();
               }

               return;
            }
      }
   }
}
