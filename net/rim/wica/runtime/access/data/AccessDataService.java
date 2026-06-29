package net.rim.wica.runtime.access.data;

import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.DataCollection;

public interface AccessDataService {
   int getDefType(int var1);

   ToIntHashtable getAllDefs();

   DataCollection getDataCollection(int var1, Wiclet var2);

   void clearDataCollection(int var1);

   void clearDataCollections();

   EnumCollection getEnumCollection();

   void activate();

   void deactivate();
}
