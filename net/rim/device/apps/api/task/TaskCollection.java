package net.rim.device.apps.api.task;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionWithVersion;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;

public interface TaskCollection extends CollectionEventSource, ReadableList, WritableSet, CollectionWithVersion {
   long TASK_TIMEBASED_OBJECT_PROVIDER_KEY;

   void update(Object var1, Object var2);

   TaskCollection getCollectionStore();
}
