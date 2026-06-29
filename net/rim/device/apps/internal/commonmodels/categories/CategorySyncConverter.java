package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

final class CategorySyncConverter implements SyncConverter {
   private static ContextObject _encodeContext = (ContextObject)(new Object(105, 19));
   private static ContextObject _decodeContext = _encodeContext.clone();

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object instanceof Object) {
         SyncBuffer syncBuffer = (SyncBuffer)(new Object(buffer, version, 0));
         return syncBuffer.addModel((RIMModel)object, _encodeContext);
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, version, uid));
      synchronized (_decodeContext) {
         _decodeContext.put(255, syncBuffer);
         SyncObject result = (SyncObject)FactoryUtil.createInstance(-3348482302610609156L, _decodeContext);
         _decodeContext.remove(255);
         return result;
      }
   }
}
