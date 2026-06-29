package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class CategoriesModelFactory extends RIMModelFactory {
   @Override
   public int getMinimumCount(Object context) {
      return !ContextObject.getFlag(context, 11) && !ContextObject.getFlag(context, 28) && !ContextObject.getFlag(context, 8) ? Integer.MIN_VALUE : 1;
   }

   @Override
   public int getMaximumCount(Object context) {
      return !ContextObject.getFlag(context, 11) && !ContextObject.getFlag(context, 28) && !ContextObject.getFlag(context, 8) ? Integer.MAX_VALUE : 1;
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof CategoriesModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 19)) {
         int fieldId = ((SyncBuffer)ContextObject.get(object, 255)).getFieldType(true);
         if (ContextObject.getFlag(object, 11)) {
            if (fieldId == 59) {
               return true;
            }

            return false;
         }

         if (ContextObject.getFlag(object, 28)) {
            if (fieldId == 17) {
               return true;
            }

            return false;
         }

         if (ContextObject.getFlag(object, 8)) {
            if (fieldId == 4) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   @Override
   public Object createInstance(Object initialData) {
      if (ContextObject.getFlag(initialData, 19)) {
         int fieldId = -1;
         if (ContextObject.getFlag(initialData, 11)) {
            fieldId = 59;
         } else if (ContextObject.getFlag(initialData, 28)) {
            fieldId = 17;
         } else if (ContextObject.getFlag(initialData, 8)) {
            fieldId = 4;
         }

         if (fieldId != -1) {
            label58:
            try {
               initialData = ((SyncBuffer)ContextObject.get(initialData, 255)).getString(fieldId, true);
            } finally {
               break label58;
            }
         }
      }

      CategoriesModel model = null;

      try {
         return new CategoriesModel(initialData);
      } finally {
         ;
      }
   }
}
