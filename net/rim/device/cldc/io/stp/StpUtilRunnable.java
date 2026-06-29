package net.rim.device.cldc.io.stp;

final class StpUtilRunnable implements Runnable {
   private Object[] _listeners;
   private String _service;
   private boolean _serviceState;
   private int _capabilities;

   protected StpUtilRunnable(Object[] listeners, String service, int capabilities, boolean serviceState) {
      this._listeners = listeners;
      this._service = service;
      this._serviceState = serviceState;
      this._capabilities = capabilities;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((StpListener)this._listeners[i]).stpServiceStateChanged(this._service, this._capabilities, this._serviceState);
         } finally {
            continue;
         }
      }
   }
}
