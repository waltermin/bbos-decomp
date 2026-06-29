package net.rim.device.internal.synchronization.ota.session;

final class Lock {
   private int _counter;
   private Thread _ownerThread;

   public final synchronized void acquire() {
      if (Thread.currentThread() != this._ownerThread) {
         while (this._counter != 0) {
            this.wait();
         }

         this._ownerThread = Thread.currentThread();
      }

      this._counter++;
   }

   public final synchronized void release() {
      if (this._ownerThread != null && this._counter != 0 && this._ownerThread == Thread.currentThread()) {
         this._counter--;
         if (this._counter == 0) {
            this.notify();
            this._ownerThread = null;
         }
      } else {
         throw new IllegalMonitorStateException();
      }
   }
}
