package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

public class AbstractArraySerializer extends AbstractSerializer {
   public void serializeArray(DataBuffer buffer, byte type, Object[] array) {
      if (array != null) {
         int size = array.length;
         ConverterUtilities.writeInt(buffer, type, size);

         for (int i = 0; i < size; i++) {
            if (array[i] != null) {
               ConverterUtilities.writeInt(buffer, 126, i);
               this.serialize(buffer, (byte)125, array[i]);
            }
         }

         ConverterUtilities.writeEmptyField(buffer, 127);
      }
   }

   public Object[] deserializeArray(DataBuffer buffer) {
      if (buffer.available() <= 0) {
         return null;
      }

      int size = ConverterUtilities.readInt(buffer);
      Object[] array = this.createArray(size);
      boolean isEndOfData = false;

      while (!isEndOfData && buffer.available() > 0) {
         int type = ConverterUtilities.getType(buffer);
         switch (type) {
            case 125:
               ConverterUtilities.skipField(buffer);
               break;
            case 126:
            default:
               int index = ConverterUtilities.readInt(buffer);
               array[index] = this.deserialize(buffer);
               break;
            case 127:
               isEndOfData = true;
               ConverterUtilities.skipField(buffer);
         }
      }

      return array;
   }

   protected Object[] createArray(int size) {
      return null;
   }
}
