package net.rim.device.apps.api.memo;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionWithVersion;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;

public interface MemoCollection extends CollectionEventSource, ReadableList, WritableSet, CollectionWithVersion {
   void update(Object var1, Object var2);
}
