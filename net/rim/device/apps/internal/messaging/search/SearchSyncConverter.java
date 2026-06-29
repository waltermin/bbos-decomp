package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

final class SearchSyncConverter implements SyncConverter {
   private static ContextObjectWR _encodeContextWR = (ContextObjectWR)(new Object(22, 19));
   private static ContextObjectWR _decodeContextWR = (ContextObjectWR)(new Object(22, 19));

   public SearchSyncConverter() {
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof Object)) {
         return false;
      }

      RIMModel model = (RIMModel)object;
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(buffer, version, 0));
      return syncBuffer.addModel(model, _encodeContextWR.getContextObject());
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version >= 1 && version <= 2) {
         if (version == 1) {
            dataBuffer = LegacyFilterConvert.translateFromBB21Format(dataBuffer);
            if (dataBuffer == null) {
               return null;
            }

            version = 2;
         }

         SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, version, uid));
         ContextObject decodeContext = _decodeContextWR.getContextObject();
         synchronized (decodeContext) {
            decodeContext.put(255, syncBuffer);
            SyncObject result = (SyncObject)FilterModelFactory.getInstance().createInstance(decodeContext);
            decodeContext.remove(255);
            return result;
         }
      } else {
         return null;
      }
   }
}
