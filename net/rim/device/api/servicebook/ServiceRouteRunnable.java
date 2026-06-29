package net.rim.device.api.servicebook;

final class ServiceRouteRunnable implements Runnable {
   private Object[] _listeners;
   private int _route;
   private boolean _routeState;

   protected ServiceRouteRunnable(Object[] listeners, int route, boolean routeState) {
      this._listeners = listeners;
      this._route = route;
      this._routeState = routeState;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            Object var10000 = this._listeners[i];
            if (this._listeners[i] instanceof ServiceRoutingListener2) {
               ((ServiceRoutingListener2)var10000).serviceRouteStateChanged(this._route, this._routeState);
            }
         } finally {
            continue;
         }
      }
   }
}
