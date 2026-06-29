package net.rim.wica.runtime.access.data.collections;

import net.rim.device.api.util.IntVector;

public interface StdCmpCollection {
   void initHandles();

   void initMappings();

   void initFieldHandlers();

   void loadItem(long var1, Object var3);

   IntVector uidsInExternalDB();

   void saveDeletedItems();

   void saveModifiedItems();

   void saveCreatedItems();

   void resetCache();
}
