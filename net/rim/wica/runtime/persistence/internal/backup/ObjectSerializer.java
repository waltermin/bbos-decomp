package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.util.SerializerUtil;

final class ObjectSerializer extends AbstractBigVectorSerializer {
   private static ObjectSerializer _instance;
   private static final byte STRING;
   private static final byte STRING_ARRAY;
   private static final byte BYTE_ARRAY;
   private static final byte INT_ARRAY;
   private static final byte LONG_ARRAY;
   private static final byte DOUBLE_ARRAY;
   private static final byte BOOLEAN_ARRAY;
   private static final byte OBJECT_ARRAY;

   static final ObjectSerializer getInstance() {
      if (_instance == null) {
         _instance = new ObjectSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   public final void serialize(DataBuffer buffer, byte type, Object obj) {
      if (obj instanceof Object) {
         SerializerUtil.writeString(buffer, (byte)0, (String)obj);
      } else if (obj instanceof Object[]) {
         StringSerializer.getInstance().serializeArray(buffer, (byte)1, (Object[])obj);
      } else if (obj instanceof byte[]) {
         SerializerUtil.writeByteArray(buffer, (byte)2, (byte[])obj);
      } else if (obj instanceof int[]) {
         SerializerUtil.writeIntArray(buffer, (byte)3, (int[])obj);
      } else if (obj instanceof long[]) {
         SerializerUtil.writeLongArray(buffer, (byte)4, (long[])obj);
      } else if (obj instanceof double[]) {
         SerializerUtil.writeDoubleArray(buffer, (byte)5, (double[])obj);
      } else if (obj instanceof boolean[]) {
         SerializerUtil.writeBooleanArray(buffer, (byte)6, (boolean[])obj);
      } else {
         if (obj instanceof Object[]) {
            this.serializeArray(buffer, (byte)7, (Object[])obj);
         }
      }
   }

   @Override
   public final Object deserialize(DataBuffer buffer) {
      int type = ConverterUtilities.getType(buffer);
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return null;
         case 0:
         default:
            return ConverterUtilities.readString(buffer);
         case 1:
            return StringSerializer.getInstance().deserializeArray(buffer);
         case 2:
            return ConverterUtilities.readByteArray(buffer);
         case 3:
            return ConverterUtilities.readIntArray(buffer);
         case 4:
            return SerializerUtil.readLongArray(buffer);
         case 5:
            return SerializerUtil.readDoubleArray(buffer);
         case 6:
            return SerializerUtil.readBooleanArray(buffer);
         case 7:
            return this.deserializeArray(buffer);
      }
   }

   private ObjectSerializer() {
   }
}
