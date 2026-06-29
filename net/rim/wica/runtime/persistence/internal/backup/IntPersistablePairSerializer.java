package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.PersDataCollectionStruct;
import net.rim.wica.runtime.persistence.PersKeylessDataCollectionStruct;
import net.rim.wica.runtime.persistence.PersStandaloneDataStruct;

final class IntPersistablePairSerializer extends AbstractArraySerializer {
   private static IntPersistablePairSerializer _instance;
   private static final byte DEF_ID;
   private static final byte PERS_STANDALONE_DATA_STRUCT;
   private static final byte PERS_DATA_COLLECTION_STRUCT;
   private static final byte PERS_KEYLESS_DATA_COLLECTION_STRUCT;

   static final IntPersistablePairSerializer getInstance() {
      if (_instance == null) {
         _instance = new IntPersistablePairSerializer();
      }

      return _instance;
   }

   static final void nullInstnace() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      IntPersistablePair pair = (IntPersistablePair)obj;
      ConverterUtilities.writeInt(buffer, 0, pair.getDefId());
      Persistable data = pair.getData();
      if (data instanceof PersStandaloneDataStruct) {
         PersStandaloneDataStructSerializer.getInstance().serialize(buffer, (byte)1, data);
      } else {
         if (data instanceof PersDataCollectionStruct) {
            if (data instanceof PersKeylessDataCollectionStruct) {
               PersKeylessDataCollectionStructSerializer.getInstance().serialize(buffer, (byte)3, data);
               return;
            }

            PersDataCollectionStructSerializer.getInstance().serialize(buffer, (byte)2, data);
         }
      }
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      IntPersistablePair pair = (IntPersistablePair)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            pair.setDefId(ConverterUtilities.readInt(buffer));
            return;
         case 1:
            pair.setData((PersStandaloneDataStruct)PersStandaloneDataStructSerializer.getInstance().deserialize(buffer));
            return;
         case 2:
            pair.setData((PersDataCollectionStruct)PersDataCollectionStructSerializer.getInstance().deserialize(buffer));
            return;
         case 3:
            pair.setData((PersKeylessDataCollectionStruct)PersKeylessDataCollectionStructSerializer.getInstance().deserialize(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new IntPersistablePair();
   }

   @Override
   protected final Object[] createArray(int size) {
      return new IntPersistablePair[size];
   }

   private IntPersistablePairSerializer() {
   }
}
