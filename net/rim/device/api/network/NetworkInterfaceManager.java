package net.rim.device.api.network;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.proxy.Proxy;

public class NetworkInterfaceManager {
   protected String _name;
   protected NetworkInterfaceInfo _info;
   protected Object _lock;
   protected boolean _status;
   private Proxy _proxy;
   private Object[] _listeners;

   public String getName() {
      return this._name;
   }

   public NetworkInterfaceInfo getInfo() {
      return this._info;
   }

   public boolean getStatus() {
      return this._status;
   }

   public void addListener(NetworkInterfaceListener listener) {
      synchronized (this._lock) {
         this._listeners = ListenerUtilities.addListener(this._listeners, listener);
      }
   }

   public void removeListener(NetworkInterfaceListener listener) {
      synchronized (this._lock) {
         this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
      }
   }

   protected NetworkInterfaceManager(String name, long guid, NetworkInterfaceInfo info) {
      this._name = name;
      this._info = info;
      this._proxy = Proxy.getInstance();
      this._lock = new Object();
      EventLogger.register(guid, "net.rim.if-" + name, 2);
   }

   protected void invokeListeners(boolean status, Object context, boolean redirect) {
      Object[] listeners;
      synchronized (this._lock) {
         listeners = this._listeners;
      }

      if (listeners != null && listeners.length > 0) {
         if (redirect) {
            this._proxy.invokeRunnable(new NetworkInterfaceManagerRunnable1(listeners, status, context, this));
            return;
         }

         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               ((NetworkInterfaceListener)listeners[i]).networkInterfaceStatusChanged(status, context, this);
            } finally {
               continue;
            }
         }
      }
   }

   protected void invokeListeners(int event, Object context, boolean redirect) {
      Object[] listeners;
      synchronized (this._lock) {
         listeners = this._listeners;
      }

      if (listeners != null && listeners.length > 0) {
         if (redirect) {
            this._proxy.invokeRunnable(new NetworkInterfaceManagerRunnable2(listeners, event, context, this));
            return;
         }

         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               ((NetworkInterfaceListener)listeners[i]).networkInterfaceEvent(event, context, this);
            } finally {
               continue;
            }
         }
      }
   }
}
