package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.api.util.IntHashtable;
import net.rim.wica.runtime.metadata.component.DataCollection;

public interface BuiltinCollection extends DataCollection {
   boolean loadItemFromDB(long var1);

   Object getDBItemFromHandle(long var1);

   boolean isItemLoaded(long var1);

   IntHashtable getObjectFieldHandlers();

   IntHashtable getLongFieldHandlers();

   IntHashtable getBooleanFieldHandlers();

   IntHashtable getIntFieldHandlers();
}
