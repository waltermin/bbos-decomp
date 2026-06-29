package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.LongHashtable;

public final class ProxyModelStoreManager {
   private LongHashtable _cache = (LongHashtable)(new Object());
   private static final long ID = -883030902214570594L;
   private static ProxyModelStoreManager _instance;

   public static final ProxyModelStore getProxyModelStore(long rootId) {
      ProxyModelStore store = (ProxyModelStore)_instance._cache.get(rootId);
      if (store == null) {
         PersistentObject po = RIMPersistentStore.getPersistentObject(rootId);
         store = (ProxyModelStore)po.getContents();
         if (store != null) {
            _instance._cache.put(rootId, store);
         }
      }

      return store;
   }

   public static final ProxyModelStoreManager getInstance() {
      return _instance;
   }

   private ProxyModelStoreManager() {
   }

   public final void register(long rootId, ProxyModelStore store) {
      this._cache.put(rootId, store);
   }

   public final ProxyModelStore deregister(long rootId) {
      return (ProxyModelStore)this._cache.remove(rootId);
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         ProxyModelStoreManager pmsm = (ProxyModelStoreManager)ar.get(-883030902214570594L);
         if (pmsm == null) {
            pmsm = new ProxyModelStoreManager();
            ar.put(-883030902214570594L, pmsm);
         }

         _instance = pmsm;
      }
   }
}
