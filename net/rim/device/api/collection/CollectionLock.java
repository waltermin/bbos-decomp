package net.rim.device.api.collection;

import net.rim.device.api.system.ApplicationRegistry;

public final class CollectionLock {
   private static CollectionLock _lockObject;
   private static final long GLOBAL_COLLECTION_LOCK_OBJECT_LUID;

   private CollectionLock() {
   }

   public static final CollectionLock getGlobalLock() {
      return _lockObject;
   }

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      _lockObject = (CollectionLock)applicationRegistry.getOrWaitFor(26644082433128156L);
      if (_lockObject == null) {
         _lockObject = new CollectionLock();
         applicationRegistry.put(26644082433128156L, _lockObject);
      }
   }
}
