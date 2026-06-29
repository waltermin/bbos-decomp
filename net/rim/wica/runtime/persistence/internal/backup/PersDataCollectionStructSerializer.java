package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.persistence.PersDataCollectionStruct;
import net.rim.wica.runtime.util.SerializerUtil;

class PersDataCollectionStructSerializer extends AbstractSerializer {
   private static PersDataCollectionStructSerializer _instance;
   private static final byte DATA_FIELDS = 0;
   private static final byte LONG_DATA = 1;
   private static final byte REF_OBJECTS = 2;
   private static final byte ID_SOURCE = 3;

   static PersDataCollectionStructSerializer getInstance() {
      if (_instance == null) {
         _instance = new PersDataCollectionStructSerializer();
      }

      return _instance;
   }

   static void nullInstance() {
      _instance = null;
   }

   @Override
   protected void serializeObject(DataBuffer buffer, Object obj) {
      PersDataCollectionStruct data = (PersDataCollectionStruct)obj;
      SerializerUtil.writeBigIntVector(buffer, (byte)0, data.getDataFields());
      SerializerUtil.writeBigLongVector(buffer, (byte)1, data.getLongData());
      ObjectSerializer.getInstance().serializeBigVector(buffer, (byte)2, data.getRefObjects());
      ConverterUtilities.writeInt(buffer, 3, data.getIdSource());
   }

   @Override
   protected void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      PersDataCollectionStruct data = (PersDataCollectionStruct)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            data.setDataFields(SerializerUtil.readBigIntVector(buffer));
            return;
         case 1:
            data.setLongData(SerializerUtil.readBigLongVector(buffer));
            return;
         case 2:
            data.setRefObjects(ObjectSerializer.getInstance().deserializeBigVector(buffer));
            return;
         case 3:
            data.setIdSource(ConverterUtilities.readInt(buffer));
      }
   }

   @Override
   protected Object createObject() {
      return new PersDataCollectionStruct();
   }
}
