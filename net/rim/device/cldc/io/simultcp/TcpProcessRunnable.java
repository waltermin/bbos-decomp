package net.rim.device.cldc.io.simultcp;

final class TcpProcessRunnable implements Runnable {
   private Thread _thread;

   protected TcpProcessRunnable(Thread thread) {
      this._thread = thread;
   }

   @Override
   public final void run() {
      this._thread.start();
   }
}
