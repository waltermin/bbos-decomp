package net.rim.device.cldc.io.srp;

final class SrpUtilsRunnable implements Runnable {
   private Object[] _listeners;
   private String _service;
   private boolean _serviceState;
   private int _capabilities;

   protected SrpUtilsRunnable(Object[] listeners, String service, int capabilities, boolean serviceState) {
      this._listeners = listeners;
      this._service = service;
      this._serviceState = serviceState;
      this._capabilities = capabilities;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((SrpListener)this._listeners[i]).srpServiceStateChanged(this._service, this._capabilities, this._serviceState);
         } finally {
            continue;
         }
      }
   }
}
