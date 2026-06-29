package net.rim.plazmic.internal.mediaengine.event;

class EventEngine$EventsRunnable implements Runnable {
   boolean _isInQueue;
   private final EventEngine this$0;

   EventEngine$EventsRunnable(EventEngine _1) {
      this.this$0 = _1;
      this._isInQueue = false;
   }

   @Override
   public void run() {
      synchronized (this.this$0._event) {
         if (!this._isInQueue) {
            return;
         }

         this._isInQueue = false;
         if (this == this.this$0._timerRunnable) {
            this.this$0._timedRunnableId = -1;
         }
      }

      if (this.this$0._isRunning) {
         this.this$0.processEvents();
      }
   }
}
