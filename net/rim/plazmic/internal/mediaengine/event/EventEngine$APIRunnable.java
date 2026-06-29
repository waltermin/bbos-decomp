package net.rim.plazmic.internal.mediaengine.event;

class EventEngine$APIRunnable implements Runnable {
   EventHeap _apiHeap;
   boolean _isInQueue;
   private final EventEngine this$0;

   EventEngine$APIRunnable(EventEngine _1) {
      this.this$0 = _1;
      this._apiHeap = new EventHeap(new EventArray());
   }

   synchronized void postEvent(Event event) {
      this._apiHeap.put(event);
   }

   private synchronized boolean getEvent(Event event) {
      if (!this._apiHeap.pop(event)) {
         this._isInQueue = false;
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void run() {
      Event event = this.this$0.getEventInstance();

      while (this.getEvent(event)) {
         switch (event._event) {
            case -1717674305:
               this.this$0.onStop();
               break;
            case -1391809431:
               this.this$0.onNewTime(event._eventParamLong);
               break;
            case -704554377:
               this.this$0.processEvents();
               break;
            case 868433339:
               this.this$0.onStart();
         }
      }

      this.this$0.releaseEventInstance(event);
   }
}
