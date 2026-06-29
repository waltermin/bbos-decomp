package net.rim.device.api.network;

final class NetworkInterfaceManagerRunnable2 implements Runnable {
   private Object[] _listeners;
   private int _event;
   private Object _context;
   private NetworkInterfaceManager _manager;

   protected NetworkInterfaceManagerRunnable2(Object[] listeners, int event, Object context, NetworkInterfaceManager manager) {
      this._listeners = listeners;
      this._event = event;
      this._context = context;
      this._manager = manager;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((NetworkInterfaceListener)this._listeners[i]).networkInterfaceEvent(this._event, this._context, this._manager);
         } finally {
            continue;
         }
      }
   }
}
