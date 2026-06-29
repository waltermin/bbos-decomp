package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class CategoryModelFactory extends RIMModelFactory {
   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof CategoryModel) {
         return true;
      } else if (ContextObject.getFlag(object, 105) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         return syncBuffer != null && syncBuffer.getFieldType(true) == 1;
      } else {
         return false;
      }
   }

   @Override
   public Object createInstance(Object initialData) {
      CategoryModel model = null;
      if (ContextObject.getFlag(initialData, 105) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            CategoryList categoryList = CategoryList.getInstance();
            String name = syncBuffer.getString(1, true);
            if (name != null) {
               model = categoryList.getCategory(name.trim());
               if (model == null) {
                  return new CategoryModel(name);
               }
            }
         } finally {
            return model;
         }
      }

      return model;
   }
}
