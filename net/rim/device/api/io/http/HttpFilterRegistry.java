package net.rim.device.api.io.http;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class HttpFilterRegistry {
   private Hashtable _filters = (Hashtable)(new Object());
   private static final long ID = 1270710104458132462L;
   private static HttpFilterRegistry _instance;

   private HttpFilterRegistry() {
   }

   public static final void registerFilter(String fqdn, String protocolPackage) {
      registerFilter(fqdn, protocolPackage, false);
   }

   public static final void registerFilter(String fqdn, String protocolPackage, boolean isLocal) {
      if (fqdn == null || protocolPackage == null || fqdn.indexOf(46) == -1 || protocolPackage.indexOf(46) == -1) {
         throw new Object();
      }

      if (!ApplicationControl.isBrowserFilterAllowed(fqdn, true)) {
         throw new Object();
      }

      HttpFilterRegistry$Item item = new HttpFilterRegistry$Item(null);
      item._package = protocolPackage;
      item._local = isLocal;
      _instance._filters.put(StringUtilities.toLowerCase(fqdn, 1701707776), item);
   }

   public static final void deregisterFilter(String fqdn) {
      if (fqdn == null || fqdn.indexOf(46) == -1) {
         throw new Object();
      }

      if (!ApplicationControl.isBrowserFilterAllowed(fqdn, true)) {
         throw new Object();
      }

      _instance._filters.remove(StringUtilities.toLowerCase(fqdn, 1701707776));
   }

   public static final String getFilter(String fqdn) {
      if (fqdn == null) {
         throw new Object();
      }

      HttpFilterRegistry$Item item = (HttpFilterRegistry$Item)_instance._filters.get(StringUtilities.toLowerCase(fqdn, 1701707776));
      return item == null ? null : item._package;
   }

   public static final boolean isLocalFilter(String fqdn) {
      if (fqdn == null) {
         throw new Object();
      }

      HttpFilterRegistry$Item item = (HttpFilterRegistry$Item)_instance._filters.get(StringUtilities.toLowerCase(fqdn, 1701707776));
      return item == null ? false : item._local;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (HttpFilterRegistry)ar.getOrWaitFor(1270710104458132462L);
      if (_instance == null) {
         _instance = new HttpFilterRegistry();
         ar.put(1270710104458132462L, _instance);
      }
   }
}
