package net.rim.wica.runtime.util;

public final class Lock {
   private boolean _locked;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void acquire() {
      while (this._locked) {
         try {
            this.wait();
         } catch (Throwable var3) {
            e.printStackTrace();
            continue;
         }
      }

      this._locked = true;
   }

   public final synchronized void release() {
      this._locked = false;
      this.notify();
   }

   public final synchronized boolean acquireWithoutBlocking() {
      if (this._locked) {
         return false;
      }

      this._locked = true;
      return true;
   }

   public final synchronized boolean acquired() {
      return this._locked;
   }
}
