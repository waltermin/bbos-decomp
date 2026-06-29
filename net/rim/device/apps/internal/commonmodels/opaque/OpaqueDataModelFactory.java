package net.rim.device.apps.internal.commonmodels.opaque;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class OpaqueDataModelFactory extends RIMModelFactory {
   @Override
   public Object createInstance(Object initialData) {
      OpaqueDataModel model = null;
      if (ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);

         try {
            int fieldType = syncBuffer.getFieldType();
            byte[] data = ConverterUtilities.readByteArray(syncBuffer.getDataBuffer(), true);
            if (fieldType != 0 && data != null) {
               model = new OpaqueDataModel(null);
               model._fieldId = fieldType;
               model._data = data;
               return model;
            }
         } finally {
            return model;
         }
      }

      return model;
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof OpaqueDataModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer != null) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }
}
