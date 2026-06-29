package net.rim.device.cldc.io.lstp;

class SerialLayer$1 implements Runnable {
   private final SerialLayer this$0;

   SerialLayer$1(SerialLayer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (this.this$0._receiveLock) {
         this.this$0._rxTimerId = -1;
         this.this$0._dataAvailable = -2;
         this.this$0._receiveLock.notifyAll();
      }
   }
}
