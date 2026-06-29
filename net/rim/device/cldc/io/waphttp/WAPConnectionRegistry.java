package net.rim.device.cldc.io.waphttp;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.datarecovery.DataRecoveryListener;
import net.rim.device.internal.system.InternalServices;

public final class WAPConnectionRegistry implements DataRecoveryListener {
   private Hashtable _connections = new Hashtable();
   private boolean _connectionAvailable = DataRecovery.getInstance().isConnectionAvailable();
   private boolean _isBlackBerryTrafficInvalid;
   private static final long SINGLETON_REGISTRATION_KEY = -4842606926651938955L;
   private static WAPConnectionRegistry _instance;

   private WAPConnectionRegistry() {
      byte[] data = Branding.getData(12295);
      int flags = data != null && data.length > 0 ? data[0] & 0xFF : 0;
      this._isBlackBerryTrafficInvalid = InternalServices.isDeviceSecure() && !DeviceInfo.isSimulator() && (flags & 8) == 0;
      DataRecovery.getInstance().addListener(this);
   }

   public static final WAPConnectionRegistry getInstance() {
      return _instance;
   }

   public static final void addConnection(WAPConnection connection) {
      WAPConnectionRegistry registry = getInstance();
      synchronized (registry._connections) {
         registry._connections.put(connection.getKey(), connection);
      }
   }

   public static final void removeConnection(WAPConnection connection) {
      WAPConnectionRegistry registry = getInstance();
      synchronized (registry._connections) {
         registry._connections.remove(connection.getKey());
      }
   }

   public static final WAPConnection getConnection(WAPConnectionParams params) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final void closeAllConnections() {
      WAPConnectionRegistry registry = getInstance();
      synchronized (registry._connections) {
         Enumeration elements = registry._connections.elements();

         while (elements.hasMoreElements()) {
            WAPConnection connection = (WAPConnection)elements.nextElement();
            connection.close();
         }
      }
   }

   public static final boolean isWAPInstalled() {
      try {
         return Class.forName("net.rim.device.cldc.io.waphttp.WAPRequestImpl") != null;
      } finally {
         ;
      }
   }

   public static final boolean isWTLSInstalled() {
      try {
         return Class.forName("net.rim.device.api.crypto.tls.wtls20.WTLS20") != null;
      } finally {
         ;
      }
   }

   @Override
   public final void dataRecoveryEventOccurred(int event, int linkType) {
      switch (event) {
         case 1:
            this._connectionAvailable = true;
         default:
            return;
         case 3:
            this._connectionAvailable = false;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (WAPConnectionRegistry)ar.getOrWaitFor(-4842606926651938955L);
      if (_instance == null) {
         _instance = new WAPConnectionRegistry();
         ar.put(-4842606926651938955L, _instance);
      }
   }
}
