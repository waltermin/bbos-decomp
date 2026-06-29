package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

public class AbstractSerializer {
   public void serialize(DataBuffer buffer, byte type, Object obj) {
      if (obj != null) {
         ConverterUtilities.writeEmptyField(buffer, type);
         this.serializeObject(buffer, obj);
         ConverterUtilities.writeEmptyField(buffer, 127);
      }
   }

   public Object deserialize(DataBuffer buffer) {
      ConverterUtilities.skipField(buffer);
      boolean isEndOfData = false;
      Object obj = this.createObject();

      while (!isEndOfData && buffer.available() > 0) {
         int type = ConverterUtilities.getType(buffer);
         if (type == 127) {
            isEndOfData = true;
            ConverterUtilities.skipField(buffer);
         } else {
            this.deserializeObjectField(buffer, obj, type);
         }
      }

      return obj;
   }

   protected void serializeObject(DataBuffer buffer, Object obj) {
   }

   protected void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      ConverterUtilities.skipField(buffer);
   }

   protected Object createObject() {
      return null;
   }
}
