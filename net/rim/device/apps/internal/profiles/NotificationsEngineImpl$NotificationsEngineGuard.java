package net.rim.device.apps.internal.profiles;

final class NotificationsEngineImpl$NotificationsEngineGuard implements Runnable {
   private final NotificationsEngineImpl this$0;

   NotificationsEngineImpl$NotificationsEngineGuard(NotificationsEngineImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._guardInvokeId = -1;
      synchronized (this.this$0._queues) {
         this.this$0._queues.notify();
      }
   }
}
