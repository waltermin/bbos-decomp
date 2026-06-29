package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.persistence.PersStandaloneDataStruct;
import net.rim.wica.runtime.util.SerializerUtil;

final class PersStandaloneDataStructSerializer extends AbstractSerializer {
   private static PersStandaloneDataStructSerializer _instance;
   private static final byte DATA_FIELDS;
   private static final byte LONG_DATA;
   private static final byte REF_OBJECTS;

   static final PersStandaloneDataStructSerializer getInstance() {
      if (_instance == null) {
         _instance = new PersStandaloneDataStructSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      PersStandaloneDataStruct data = (PersStandaloneDataStruct)obj;
      SerializerUtil.writeIntArray(buffer, (byte)0, data.getDataFields());
      SerializerUtil.writeLongArray(buffer, (byte)1, data.getLongData());
      ObjectSerializer.getInstance().serializeArray(buffer, (byte)2, data.getRefObjects());
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      PersStandaloneDataStruct data = (PersStandaloneDataStruct)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            data.setDataFields(ConverterUtilities.readIntArray(buffer));
            return;
         case 1:
            data.setLongData(SerializerUtil.readLongArray(buffer));
            return;
         case 2:
            data.setRefObjects(ObjectSerializer.getInstance().deserializeArray(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new PersStandaloneDataStruct();
   }

   private PersStandaloneDataStructSerializer() {
   }
}
