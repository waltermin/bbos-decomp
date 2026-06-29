package net.rim.device.api.servicebook;

final class ServiceRoutingRunnable implements Runnable {
   private Object[] _listeners;
   private String _service;
   private boolean _serviceState;
   private boolean _capabilitiesChanged;

   protected ServiceRoutingRunnable(Object[] listeners, String service, boolean serviceState, boolean capabilitiesChanged) {
      this._listeners = listeners;
      this._service = service;
      this._serviceState = serviceState;
      this._capabilitiesChanged = capabilitiesChanged;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((ServiceRoutingListener)this._listeners[i]).serviceRoutingStateChanged(this._service, this._serviceState);
            if (this._capabilitiesChanged) {
               Object var10000 = this._listeners[i];
               if (this._listeners[i] instanceof ServiceRoutingListener2) {
                  ((ServiceRoutingListener2)var10000).serviceRoutingCapabilitiesChanged(this._service);
               }
            }
         } finally {
            continue;
         }
      }
   }
}
