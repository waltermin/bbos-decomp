package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.wica.runtime.persistence.PersKeylessDataCollectionStruct;

final class PersKeylessDataCollectionStructSerializer extends PersDataCollectionStructSerializer {
   private static PersKeylessDataCollectionStructSerializer _instance;
   private static final byte PERSISTENT_REF_COUNTS = 4;

   static final PersDataCollectionStructSerializer getInstance() {
      if (_instance == null) {
         _instance = new PersKeylessDataCollectionStructSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      PersKeylessDataCollectionStruct data = (PersKeylessDataCollectionStruct)obj;
      LongIntHashtableSerializer.getInstance().serialize(buffer, (byte)4, data._persistentRefCounts);
      super.serializeObject(buffer, obj);
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      PersKeylessDataCollectionStruct data = (PersKeylessDataCollectionStruct)obj;
      switch (type) {
         case 4:
            data._persistentRefCounts = (LongIntHashtable)LongIntHashtableSerializer.getInstance().deserialize(buffer);
            return;
         default:
            super.deserializeObjectField(buffer, obj, type);
      }
   }

   @Override
   protected final Object createObject() {
      return new PersKeylessDataCollectionStruct();
   }

   private PersKeylessDataCollectionStructSerializer() {
   }
}
