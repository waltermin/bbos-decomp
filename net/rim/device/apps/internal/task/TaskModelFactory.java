package net.rim.device.apps.internal.task;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

final class TaskModelFactory extends RIMModelFactory {
   private static RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();

   @Override
   public final Object createInstance(Object initialData) {
      TaskModelImpl model = null;
      if (ContextObject.getFlag(initialData, 28) && ContextObject.getFlag(initialData, 19) && initialData != null) {
         ContextObject context = (ContextObject)initialData;
         model = new TaskModelImpl();

         label39:
         try {
            SyncBuffer syncBuffer = (SyncBuffer)context.get(255);
            RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(7798410905730545828L);
            RIMModelFactoryCache.addToModelWithCache(_factoryCache, factories, null, syncBuffer, model, context);
            int uid = syncBuffer.getUID();
            if (uid != 0) {
               model._uid = uid;
            } else {
               model._uid = UIDGenerator.getUID(UIDGenerator.getUniqueScopingValue());
            }

            if (model.getTaskDataModel() == null) {
               TaskDataModel dataModel = new TaskDataModel(initialData);
               model.add(dataModel);
            }
         } finally {
            break label39;
         }

         ObjectGroup.createGroupIgnoreTooBig(model);
         return model;
      } else {
         model = new TaskModelImpl();
         TaskDataModel dataModel = new TaskDataModel(null);
         model.add(dataModel);
         return model;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public final boolean recognize(Object object) {
      return object instanceof TaskModelImpl ? true : ContextObject.getFlag(object, 28) && ContextObject.getFlag(object, 19);
   }
}
