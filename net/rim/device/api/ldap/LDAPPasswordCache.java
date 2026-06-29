package net.rim.device.api.ldap;

import java.util.Hashtable;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;

public final class LDAPPasswordCache implements MemoryCleanerListener {
   private PersistentObject _persist = RIMPersistentStore.getPersistentObject(-3467343715701600366L);
   private Hashtable _mapToUsernames;
   private Hashtable _mapToPasswords;
   private static final long LOCK = -650205280484353649L;
   private static final long PERSIST = -3467343715701600366L;
   private static final long ID = 7688841469514570471L;

   private LDAPPasswordCache() {
      synchronized (this._persist) {
         if (this._persist.getContents() == null) {
            this._persist.setContents(new Hashtable(), 4801362);
            this._persist.commit();
         }
      }

      this._mapToUsernames = (Hashtable)this._persist.getContents();
      this._mapToPasswords = new Hashtable();
      MemoryCleanerDaemon.addListener(this);
   }

   public static final LDAPPasswordCache getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry.getObject(-650205280484353649L)) {
         LDAPPasswordCache cache = (LDAPPasswordCache)registry.get(7688841469514570471L);
         if (cache == null) {
            cache = new LDAPPasswordCache();
            registry.put(7688841469514570471L, cache);
         }

         return cache;
      }
   }

   public final void setPassword(String server, String baseQuery, String userDN, String password) {
      if (server != null && baseQuery != null && userDN != null && password != null) {
         String concat = StringUtilities.toLowerCase(server.concat(baseQuery), 1701707776);
         this._mapToUsernames.put(concat, userDN);
         this._mapToPasswords.put(concat, password);
         this._persist.commit();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final String getUserDN(String server, String baseQuery) {
      if (server != null && baseQuery != null) {
         String concat = StringUtilities.toLowerCase(server.concat(baseQuery), 1701707776);
         return (String)this._mapToUsernames.get(concat);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final String getPassword(String server, String baseQuery) {
      if (server != null && baseQuery != null) {
         String concat = StringUtilities.toLowerCase(server.concat(baseQuery), 1701707776);
         return (String)this._mapToPasswords.get(concat);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void cleanPassword(String server, String baseQuery) {
      if (server != null && baseQuery != null) {
         String concat = StringUtilities.toLowerCase(server.concat(baseQuery), 1701707776);
         this._mapToPasswords.remove(concat);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final boolean cleanNow(int event) {
      switch (event) {
         case 3:
         case 5:
            return false;
         case 4:
         case 6:
         case 7:
         case 8:
         default:
            this.cleanPasswords();
            return true;
      }
   }

   @Override
   public final String getDescription() {
      return CommonResource.getString(10031);
   }

   private final void cleanPasswords() {
      this._mapToPasswords.clear();
   }
}
