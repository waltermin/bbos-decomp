package net.rim.device.api.network;

final class NetworkInterfaceManagerRunnable1 implements Runnable {
   private Object[] _listeners;
   private boolean _status;
   private Object _context;
   private NetworkInterfaceManager _manager;

   protected NetworkInterfaceManagerRunnable1(Object[] listeners, boolean status, Object context, NetworkInterfaceManager manager) {
      this._listeners = listeners;
      this._status = status;
      this._context = context;
      this._manager = manager;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((NetworkInterfaceListener)this._listeners[i]).networkInterfaceStatusChanged(this._status, this._context, this._manager);
         } finally {
            continue;
         }
      }
   }
}
