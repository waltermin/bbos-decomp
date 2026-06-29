package net.rim.device.apps.api.framework.model;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

public class RIMModelSyncConverter implements SyncConverter {
   private ContextObject _encodeContext;
   private ContextObject _decodeContext;
   private long _factoryId;

   public RIMModelSyncConverter(int modelContext, long factoryId) {
      this._encodeContext = new ContextObject(modelContext, 19);
      this._decodeContext = this._encodeContext.clone();
      this._factoryId = factoryId;
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object instanceof RIMModel) {
         SyncBuffer syncBuffer = new SyncBuffer(buffer, version, 0);
         RIMModel model = (RIMModel)object;
         return syncBuffer.addModel(model, this._encodeContext);
      } else {
         return false;
      }
   }

   @Override
   public SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);
      RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(this._factoryId);
      SyncObject result = null;
      synchronized (this._decodeContext) {
         this._decodeContext.put(255, syncBuffer);
         int n = factories.length;

         for (int i = 0; i < n; i++) {
            int position = syncBuffer.getPosition();
            RIMModelFactory factory = factories[i];
            if (factory.recognize(this._decodeContext)) {
               syncBuffer.setPosition(position);
               result = (SyncObject)factory.createInstance(this._decodeContext);
               break;
            }

            syncBuffer.setPosition(position);
         }

         this._decodeContext.remove(255);
         return result;
      }
   }
}
