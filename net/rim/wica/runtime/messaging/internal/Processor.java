package net.rim.wica.runtime.messaging.internal;

public class Processor implements Runnable {
   protected Scheduler _scheduler;
   protected boolean _scheduled;

   public Processor() {
   }

   public Processor(Scheduler scheduler) {
      this._scheduler = scheduler;
   }

   public synchronized void schedule() {
      if (this.shouldSchedule()) {
         this._scheduled = true;
         this._scheduler.schedule(this);
      }
   }

   protected boolean shouldSchedule() {
      return false;
   }

   public void reschedule() {
      this._scheduled = false;
      if (this.shouldSchedule()) {
         this.schedule();
      }
   }

   @Override
   public void run() {
      throw null;
   }
}
