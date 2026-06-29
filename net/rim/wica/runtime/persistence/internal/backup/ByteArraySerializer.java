package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.util.ByteArray;
import net.rim.wica.runtime.util.SerializerUtil;

final class ByteArraySerializer extends AbstractBigVectorSerializer {
   private static ByteArraySerializer _instance;

   static final ByteArraySerializer getInstance() {
      if (_instance == null) {
         _instance = new ByteArraySerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   public final void serialize(DataBuffer buffer, byte type, Object obj) {
      SerializerUtil.writeByteArray(buffer, type, ((ByteArray)obj).getArray());
   }

   @Override
   public final Object deserialize(DataBuffer buffer) {
      return new ByteArray(ConverterUtilities.readByteArray(buffer));
   }

   private ByteArraySerializer() {
   }
}
