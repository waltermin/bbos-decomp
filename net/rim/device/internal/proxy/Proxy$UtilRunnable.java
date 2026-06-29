package net.rim.device.internal.proxy;

final class Proxy$UtilRunnable implements Runnable {
   private Thread _thread;

   protected Proxy$UtilRunnable(Runnable runnable, boolean isThread) {
      this._thread = isThread ? (Thread)runnable : new Thread(runnable);
   }

   @Override
   public final void run() {
      this._thread.start();
   }
}
