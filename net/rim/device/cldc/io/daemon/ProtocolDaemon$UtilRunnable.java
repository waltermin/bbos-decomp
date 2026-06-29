package net.rim.device.cldc.io.daemon;

final class ProtocolDaemon$UtilRunnable implements Runnable {
   private Thread _thread;

   protected ProtocolDaemon$UtilRunnable(Runnable runnable, boolean isThread) {
      this._thread = isThread ? (Thread)runnable : new Thread(runnable);
   }

   @Override
   public final void run() {
      this._thread.start();
   }
}
