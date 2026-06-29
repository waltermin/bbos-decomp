package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.CyclicQueue;

public final class SuplEventMonitor {
   private static CyclicQueue _queue;

   SuplEventMonitor() {
      _queue = (CyclicQueue)(new Object());
   }

   public final synchronized SuplEvent getEvent() {
      while (_queue.isEmpty()) {
         try {
            this.wait();
         } finally {
            continue;
         }
      }

      return (SuplEvent)_queue.dequeue();
   }

   public final synchronized void issueEvent(SuplEvent event) {
      _queue.enqueue(event);
      this.notifyAll();
   }

   public static final void initialize() {
      _queue.flush();
   }
}
