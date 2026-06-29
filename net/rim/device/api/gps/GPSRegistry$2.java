package net.rim.device.api.gps;

class GPSRegistry$2 extends Thread {
   private final GPSRegistry this$0;

   GPSRegistry$2(GPSRegistry _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      try {
         Thread.sleep(2000);
      } catch (InterruptedException var2) {
      }

      this.this$0.restartCDMAAssistedLocationUpdate();
   }
}
