package net.rim.device.apps.internal.task;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class TaskDataModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object initialData) {
      return new TaskDataModel(initialData);
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof TaskDataModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 28) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer != null) {
            int fieldType = syncBuffer.getFieldType();
            switch (fieldType) {
               case 8:
               case 9:
               case 10:
               case 12:
               case 14:
               case 15:
               case 16:
               case 31:
                  return true;
            }
         }
      }

      return false;
   }
}
