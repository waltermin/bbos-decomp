package net.rim.device.api.synchronization;

public interface MultiServiceSyncCollection extends SyncCollection {
   void setSid(long var1);

   long getSid();

   void setDefault(boolean var1);

   boolean isDefault();

   MultiServiceSyncCollection getCollection(long var1);
}
