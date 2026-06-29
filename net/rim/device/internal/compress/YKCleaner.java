package net.rim.device.internal.compress;

import net.rim.device.api.system.ApplicationProcess;
import net.rim.vm.Process;

final class YKCleaner implements Runnable {
   private boolean _registered;
   private Object _objToClean;

   public YKCleaner(Object obj) {
      this._objToClean = obj;
   }

   public final void register() {
      if (!this._registered) {
         ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this);
         this._registered = true;
      }
   }

   public final void unregister() {
      if (this._registered) {
         ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this);
         this._registered = false;
      }
   }

   @Override
   public final void run() {
      this._registered = false;
      if (!(this._objToClean instanceof YKDecode)) {
         if (this._objToClean instanceof YKEncode) {
            ((YKEncode)this._objToClean).yk_uninit();
         }
      } else {
         ((YKDecode)this._objToClean).yk_uninit();
      }
   }
}
