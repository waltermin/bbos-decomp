package net.rim.device.apps.api.messaging.messagelist;

import net.rim.vm.Memory;

final class DateSortedSeparatedMessageArray$GCRunnable implements Runnable {
   private DateSortedSeparatedMessageArray$GCRunnable() {
   }

   @Override
   public final void run() {
      Memory.supressHourglass(true);
      Memory.persistentGC();
   }

   DateSortedSeparatedMessageArray$GCRunnable(DateSortedSeparatedMessageArray$1 x0) {
      this();
   }
}
