package net.rim.device.cldc.io.lstp;

final class LstpUtilRunnable implements Runnable {
   private Object[] _listeners;
   private boolean _linkState;

   protected LstpUtilRunnable(Object[] listeners, boolean linkState) {
      this._listeners = listeners;
      this._linkState = linkState;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((LstpListener)this._listeners[i]).lstpLinkStateChanged(this._linkState);
         } finally {
            continue;
         }
      }
   }
}
