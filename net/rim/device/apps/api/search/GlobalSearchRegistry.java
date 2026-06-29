package net.rim.device.apps.api.search;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;

public final class GlobalSearchRegistry {
   private static LongHashtable _registry = ApplicationRegistry.getApplicationRegistry().getLongHashtable(-8582818211835660816L);
   private static final long GLOBAL_SEARCH_REGISTRY_ID = -8582818211835660816L;

   public static final synchronized void register(long registrationId, Searchable searchable) {
      _registry.put(registrationId, searchable);
   }

   public static final synchronized void unregister(long registrationId) {
      _registry.remove(registrationId);
   }

   public static final synchronized long[] getRegistrationIds() {
      long[] registrationIds = new long[_registry.size()];
      int i = 0;
      LongEnumeration e = _registry.keys();

      while (e.hasMoreElements()) {
         registrationIds[i++] = e.nextElement();
      }

      return registrationIds;
   }

   public static final synchronized Searchable getSearchable(long registrationId) {
      return (Searchable)_registry.get(registrationId);
   }
}
