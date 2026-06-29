package net.rim.device.apps.internal.calendar.eventprovider;

class EventViewer$UpdateLookupFields implements Runnable {
   private boolean _finished;
   private final EventViewer this$0;

   private EventViewer$UpdateLookupFields(EventViewer _1) {
      this.this$0 = _1;
      this._finished = true;
   }

   private synchronized boolean notStarted() {
      boolean finished = this._finished;
      this._finished = false;
      return finished;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         this.this$0.updateLookupFields();
         var9 = false;
      } finally {
         if (var9) {
            synchronized (this) {
               this._finished = true;
            }
         }
      }

      synchronized (this) {
         this._finished = true;
      }
   }

   EventViewer$UpdateLookupFields(EventViewer x0, EventViewer$1 x1) {
      this(x0);
   }
}
