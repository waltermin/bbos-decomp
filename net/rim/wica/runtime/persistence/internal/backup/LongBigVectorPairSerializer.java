package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class LongBigVectorPairSerializer extends AbstractArraySerializer {
   private static LongBigVectorPairSerializer _instance;
   private static final byte LONG;
   private static final byte BIG_VECTOR;

   static final LongBigVectorPairSerializer getInstance() {
      if (_instance == null) {
         _instance = new LongBigVectorPairSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      LongBigVectorPair pair = (LongBigVectorPair)obj;
      ConverterUtilities.writeLong(buffer, 0, pair.getLong());
      ByteArraySerializer.getInstance().serializeBigVector(buffer, (byte)1, pair.getBigVector());
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      LongBigVectorPair pair = (LongBigVectorPair)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            pair.setLong(ConverterUtilities.readLong(buffer));
            return;
         case 1:
            pair.setBigVector(ByteArraySerializer.getInstance().deserializeBigVector(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new LongBigVectorPair();
   }

   @Override
   protected final Object[] createArray(int size) {
      return new LongBigVectorPair[size];
   }

   private LongBigVectorPairSerializer() {
   }
}
