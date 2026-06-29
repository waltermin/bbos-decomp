package net.rim.device.cldc.io.srp;

final class SrpUtilsRunnable2 implements Runnable {
   private Object[] _listeners;
   private boolean _routeState;
   private int _connectionType;
   private int _linkType;

   protected SrpUtilsRunnable2(Object[] listeners, int linkType, int connectionType, boolean routeState) {
      this._listeners = listeners;
      this._routeState = routeState;
      this._connectionType = connectionType;
      this._linkType = linkType;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((SrpListener)this._listeners[i]).srpRouteStateChanged(this._linkType, this._connectionType, this._routeState);
         } finally {
            continue;
         }
      }
   }
}
