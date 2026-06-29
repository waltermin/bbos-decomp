package net.rim.device.internal.io.store;

import java.io.OutputStream;
import net.rim.vm.WeakReference;

class AutoCloseOutputStream$Helper {
   private WeakReference _autoClosing;
   private OutputStream _underlying;

   AutoCloseOutputStream$Helper(AutoCloseOutputStream autoClosing, OutputStream underlying) {
      if (autoClosing != null && underlying != null) {
         this._autoClosing = new WeakReference(autoClosing);
         this._underlying = underlying;
      } else {
         throw new NullPointerException();
      }
   }

   public void close() {
      if (this._underlying != null) {
         this._underlying.close();
         this._underlying = null;
      }
   }

   boolean isDiscarded() {
      try {
         AutoCloseOutputStream stream = (AutoCloseOutputStream)this._autoClosing.get();
         if (stream == null) {
            this.close();
            return true;
         } else {
            return false;
         }
      } finally {
         this._underlying = null;
         return true;
      }
   }
}
