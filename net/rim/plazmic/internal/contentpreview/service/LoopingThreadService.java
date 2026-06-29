package net.rim.plazmic.internal.contentpreview.service;

public final class LoopingThreadService implements Service {
   private final Object _syncStartStop = new Object();
   private final Object _syncNext = new Object();
   private final boolean _loopWait;
   private final Runnable _task;
   private boolean _enabled = false;
   private boolean _running = false;
   private boolean _next = false;
   public static final String rcsid;

   public LoopingThreadService(Runnable task, boolean wait) {
      this._task = task;
      this._loopWait = wait;
   }

   public final synchronized void startService() {
      this.startService(true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void startService(boolean wait) {
      if (!this._enabled && !this.isRunning()) {
         this._enabled = true;
         ((Thread)(new Object(new LoopingThreadService$Loop(this, null)))).start();
         if (wait) {
            try {
               this.waitForStart();
            } catch (Throwable var4) {
               throw new ServiceException(e);
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void stopService(boolean wait) {
      if (this._enabled) {
         this._enabled = false;
         if (this._loopWait) {
            this.next();
         }

         if (wait) {
            try {
               this.waitForStop();
            } catch (Throwable var4) {
               throw new ServiceException(e);
            }
         }
      }
   }

   public final void waitForStart() {
      synchronized (this._syncStartStop) {
         while (!this.isRunning()) {
            this._syncStartStop.wait();
         }
      }
   }

   public final void waitForStop() {
      synchronized (this._syncStartStop) {
         while (this.isRunning()) {
            this._syncStartStop.wait();
         }
      }
   }

   public final void next() {
      if (this._loopWait) {
         synchronized (this._syncNext) {
            if (!this._next) {
               this._next = true;
               this._syncNext.notify();
            }
         }
      } else {
         throw new Object("The method next() is not applicable to a LoopingThreadService that does not wait.");
      }
   }

   public final boolean isRunning() {
      synchronized (this._syncStartStop) {
         return this._running;
      }
   }

   public final synchronized boolean isEnabled() {
      return this._enabled;
   }
}
