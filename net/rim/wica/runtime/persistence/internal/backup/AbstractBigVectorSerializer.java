package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.DataBuffer;

public class AbstractBigVectorSerializer extends AbstractArraySerializer {
   void serializeBigVector(DataBuffer buffer, byte type, BigVector bigVector) {
      if (bigVector != null) {
         Object[] array = new Object[bigVector.size()];
         bigVector.copyInto(0, array.length, array, 0);
         this.serializeArray(buffer, type, array);
      }
   }

   BigVector deserializeBigVector(DataBuffer buffer) {
      BigVector bigVector = null;
      Object[] array = this.deserializeArray(buffer);
      if (array != null) {
         bigVector = new BigVector(array.length);
         bigVector.addElements(array);
      }

      return bigVector;
   }

   @Override
   protected Object[] createArray(int size) {
      return new Object[size];
   }
}
