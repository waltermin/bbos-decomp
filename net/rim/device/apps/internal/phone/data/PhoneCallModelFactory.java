package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.vm.DirtyBits;

class PhoneCallModelFactory extends RIMModelFactory {
   private static RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();
   private static RIMModelFactory[] _factories = RIMModelFactoryRepository.getModelFactories(-5829986326706945081L);
   private static Factory _bodyModelFactory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(2096811533660483L);

   private PhoneCallModelImpl createSubclassPhoneCallModel(SyncBuffer syncBuffer) {
      Factory[] subclassFactories = RIMModelFactoryRepository.getModelFactories(1796461360838668029L);
      if (subclassFactories != null) {
         for (int i = 0; i < subclassFactories.length; i++) {
            Factory f = subclassFactories[i];
            Object o = f.createInstance(syncBuffer);
            if (o instanceof PhoneCallModelImpl) {
               return (PhoneCallModelImpl)o;
            }
         }
      }

      return null;
   }

   @Override
   public Object createInstance(Object initialData) {
      PhoneCallModelImpl model = null;
      boolean phone = ContextObject.getFlag(initialData, 20);
      if (initialData instanceof PhoneCallInitialData) {
         model = new PhoneCallModelImpl(initialData);
      } else if (phone && ContextObject.getFlag(initialData, 19)) {
         ContextObject context = (ContextObject)initialData;
         SyncBuffer syncBuffer = (SyncBuffer)context.get(255);

         label76:
         try {
            int bufferPosition = syncBuffer.getPosition();
            PhoneCallModelImpl subclassModel = this.createSubclassPhoneCallModel(syncBuffer);
            if (subclassModel != null) {
               model = subclassModel;
            } else {
               model = new PhoneCallModelImpl();
            }

            syncBuffer.setPosition(bufferPosition);
            syncBuffer.getString(112, true);
            model.setType((byte)syncBuffer.getInt(2, true));
            model.setElapsedTime(syncBuffer.getInt(3, true));
            model.setTimeStamp(syncBuffer.getLong(4, true));
            model.setRefId(syncBuffer.getInt(5, true));
            model.setErrorCode(syncBuffer.getInt(6, true));
            model._uid = syncBuffer.getInt(7, true);
            if (syncBuffer.containsType(16)) {
               model.setLineId(syncBuffer.getInt(16, true));
            }

            boolean oldCallLogFlag = PhoneUtilities.getPrivateFlag(context, 49);
            PhoneUtilities.setPrivateFlag(context, 49);
            RIMModelFactoryCache.addToModelWithCache(_factoryCache, _factories, null, syncBuffer, model, context);
            if (!oldCallLogFlag) {
               PhoneUtilities.clearPrivateFlag(context, 49);
            }

            if (model.size() > 2) {
               Object obj = model.getAt(2);
               if (_bodyModelFactory instanceof Object && !((Recognizer)_bodyModelFactory).recognize(obj)) {
                  obj = _bodyModelFactory.createInstance(initialData);
                  model.add(obj);
               }
            }

            int uid = syncBuffer.getUID();
            if (uid != 0) {
               model._uid = uid;
            }

            if (subclassModel != null) {
               syncBuffer.setPosition(0);
               subclassModel.addSyncSubmembers(syncBuffer);
            }
         } finally {
            break label76;
         }

         PersistentObject.commit(model);
      } else {
         model = new PhoneCallModelImpl(initialData);
      }

      DirtyBits.setDirty(model);
      return model;
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof PhoneCallModelImpl) {
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
