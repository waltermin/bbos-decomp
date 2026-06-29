package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;

public interface WicletStore {
   WicletInfo getInfo();

   Object loadDefinitions();

   byte[] loadMappingTable();

   int getMetadataFlashSize();

   int getDataFlashSize();

   int getCacheFlashSize();

   String getMemoryStatistics();

   byte[] getPackage();

   void storeData(int var1, Persistable var2);

   Persistable loadData(int var1);

   void storeDataStatus(boolean var1);

   boolean getDataStatus();

   void wipeData();

   void copyData(WicletStore var1);

   IntEnumeration loadDataDefinitionIds();

   void save();

   void storeApplication(WicletInfo var1, byte[] var2);

   void storeApplication(
      WicletInfo var1,
      byte[] var2,
      ToIntHashtable var3,
      int var4,
      ComponentDefStruct var5,
      ComponentDefStruct var6,
      ComponentDefStruct var7,
      ComponentDefStruct var8,
      Resource[] var9,
      byte[] var10
   );

   void storeResource(Resource var1);

   Resource getResource(String var1);

   int freeResources();
}
