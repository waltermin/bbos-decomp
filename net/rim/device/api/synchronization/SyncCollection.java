package net.rim.device.api.synchronization;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.i18n.Locale;

public interface SyncCollection extends Collection {
   boolean addSyncObject(SyncObject var1);

   boolean updateSyncObject(SyncObject var1, SyncObject var2);

   boolean removeSyncObject(SyncObject var1);

   boolean removeAllSyncObjects();

   SyncObject[] getSyncObjects();

   SyncObject getSyncObject(int var1);

   boolean isSyncObjectDirty(SyncObject var1);

   void setSyncObjectDirty(SyncObject var1);

   void clearSyncObjectDirty(SyncObject var1);

   int getSyncObjectCount();

   int getSyncVersion();

   String getSyncName();

   String getSyncName(Locale var1);

   SyncConverter getSyncConverter();

   void beginTransaction();

   void endTransaction();
}
