package net.rim.device.apps.internal.browser.util;

public final class Mutex {
   private boolean _isLocked;

   public final synchronized void waitForLock() {
      while (this._isLocked) {
         try {
            this.wait();
         } finally {
            continue;
         }
      }

      this._isLocked = true;
   }

   public final synchronized boolean tryLock() {
      if (this._isLocked) {
         return false;
      }

      this._isLocked = true;
      return true;
   }

   public final synchronized void releaseLock() {
      this._isLocked = false;
      this.notify();
   }

   public final synchronized boolean lockAcquired() {
      return this._isLocked;
   }
}
