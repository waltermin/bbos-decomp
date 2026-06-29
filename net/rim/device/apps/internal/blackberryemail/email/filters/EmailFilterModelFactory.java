package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

public final class EmailFilterModelFactory extends RIMModelFactory {
   private static EmailFilterModelFactory _singletonInstance = new EmailFilterModelFactory();
   private static RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();

   private EmailFilterModelFactory() {
   }

   public static final synchronized EmailFilterModelFactory getInstance() {
      return _singletonInstance;
   }

   @Override
   public final Object createInstance(Object initialData) {
      return this.createInstance(initialData, 0);
   }

   public final Object createInstance(Object initialData, int type) {
      EmailFilterModelImpl filterModel;
      if (ContextObject.getFlag(initialData, 33) && ContextObject.getFlag(initialData, 19)) {
         filterModel = new EmailFilterModelImpl();
         ContextObject context = (ContextObject)initialData;
         filterModel._fields.ensureCapacity(16);

         label54:
         try {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
            RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(-7388907038055180696L);
            RIMModelFactoryCache.addToModelWithCache(_factoryCache, factories, null, syncBuffer, filterModel, context);
            int uid = syncBuffer.getUID();
            if (uid != 0) {
               filterModel._uid = uid;
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(2)) {
               filterModel._order = syncBuffer.getInt(2, true);
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(3)) {
               filterModel._enabled = syncBuffer.getInt(3, true) == 1;
            }
         } finally {
            break label54;
         }

         filterModel._fields.trimToSize();
      } else {
         filterModel = new EmailFilterModelImpl(initialData, type);
      }

      ObjectGroup.createGroupIgnoreTooBig(filterModel);
      return filterModel;
   }

   @Override
   public final boolean recognize(Object object) {
      return object instanceof EmailFilterModelImpl ? true : ContextObject.getFlag(object, 33) && ContextObject.getFlag(object, 19);
   }
}
