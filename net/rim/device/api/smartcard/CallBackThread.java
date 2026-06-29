package net.rim.device.api.smartcard;

class CallBackThread extends Thread {
   Function _function;
   Object _lock = new Object();
   boolean _exitThread = false;
   boolean _threadRunning = false;

   public CallBackThread() {
   }

   public void exitThead() {
      this._exitThread = true;
      synchronized (this._lock) {
         this._lock.notify();
      }
   }

   public synchronized void go(Function function) {
      this._function = function;
      synchronized (this._lock) {
         if (!this._threadRunning) {
            label56:
            try {
               this._lock.wait();
            } finally {
               break label56;
            }
         }

         this._lock.notify();

         try {
            this._lock.wait();
         } finally {
            return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      synchronized (this._lock) {
         for (this._threadRunning = true; !this._exitThread; this._lock.notify()) {
            this._lock.notify();

            label81:
            try {
               this._lock.wait();
            } finally {
               break label81;
            }

            if (this._exitThread) {
               return;
            }

            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               this._function.run();
               var8 = false;
            } finally {
               if (var8) {
                  this._lock.notify();
               }
            }
         }
      }
   }
}
