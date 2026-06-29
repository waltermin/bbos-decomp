package net.rim.device.api.network;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public final class NetworkInterfaceFactory {
   private Hashtable _managers = (Hashtable)(new Object());
   private static final long GUID;

   public static final NetworkInterfaceFactory getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      NetworkInterfaceFactory nif = (NetworkInterfaceFactory)ar.getOrWaitFor(-3496055574688251758L);
      if (nif == null) {
         nif = new NetworkInterfaceFactory();
         ar.put(-3496055574688251758L, nif);
      }

      return nif;
   }

   public final NetworkInterfaceManager getManager(String name) {
      return (NetworkInterfaceManager)this._managers.get(name);
   }

   public final Enumeration getRegisteredManagerNames() {
      return this._managers.keys();
   }

   public final void registerManager(NetworkInterfaceManager manager) {
      this._managers.put(manager.getName(), manager);
   }

   private NetworkInterfaceFactory() {
   }
}
