package net.rim.device.apps.internal.calendar.viewer;

final class CollectionMonitor$CollectionChangedRunnable implements Runnable {
   private final CollectionMonitor this$0;

   CollectionMonitor$CollectionChangedRunnable(CollectionMonitor _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.collectionChanged();
   }
}
