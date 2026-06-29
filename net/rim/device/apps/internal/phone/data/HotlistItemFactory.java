package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

class HotlistItemFactory extends RIMModelFactory {
   private static RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();

   @Override
   public Object createInstance(Object initialData) {
      HotlistItem model = null;
      if (ContextObject.getFlag(initialData, 20) && ContextObject.getFlag(initialData, 19)) {
         ContextObject context = (ContextObject)initialData;
         SyncBuffer syncBuffer = (SyncBuffer)context.get(255);

         label31:
         try {
            model = new HotlistItem();
            syncBuffer.getString(104, true);
            model.setHitCount(syncBuffer.getInt(2, true));
            model.setLastCallTime(syncBuffer.getLong(3, true));
            model.setATBC(syncBuffer.getLong(4, true));
            model.setSortValue(syncBuffer.getLong(5, true));
            model._uid = syncBuffer.getInt(6, true);
            RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(-3466239368616563929L);
            RIMModelFactoryCache.addToModelWithCache(_factoryCache, factories, null, syncBuffer, model, context);
            int uid = syncBuffer.getUID();
            if (uid != 0) {
               model._uid = uid;
            }
         } finally {
            break label31;
         }

         PersistentObject.commit(model);
         return model;
      } else {
         return new HotlistItem();
      }
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof HotlistItem) {
         return true;
      } else if (ContextObject.getFlag(object, 20) && ContextObject.getFlag(object, 19)) {
         ContextObject context = (ContextObject)object;
         SyncBuffer syncBuffer = (SyncBuffer)context.get(255);
         return syncBuffer.getFieldType() == 1;
      } else {
         return false;
      }
   }
}
