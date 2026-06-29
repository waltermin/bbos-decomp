package net.rim.device.api.synchronization;

import net.rim.vm.Memory;

public final class SyncCollectionStatisticsManager {
   private static final boolean DEBUG = false;

   public static final int getSyncCollectionSize(SyncCollection syncCollection) {
      SyncObject[] syncObjects = syncCollection.getSyncObjects();
      return Memory.objectSize(syncObjects);
   }
}
