package net.rim.device.internal.system;

final class LEDEventProcessor$LEDEngineGuard implements Runnable {
   private final LEDEventProcessor this$0;

   LEDEventProcessor$LEDEngineGuard(LEDEventProcessor _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (this.this$0._events) {
         this.this$0._events.notify();
         this.this$0._guardInvokeId = -1;
      }
   }
}
