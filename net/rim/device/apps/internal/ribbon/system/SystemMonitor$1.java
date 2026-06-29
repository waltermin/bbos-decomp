package net.rim.device.apps.internal.ribbon.system;

final class SystemMonitor$1 implements Runnable {
   private final SystemMonitor this$0;

   SystemMonitor$1(SystemMonitor _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.cardUpdated();
   }
}
