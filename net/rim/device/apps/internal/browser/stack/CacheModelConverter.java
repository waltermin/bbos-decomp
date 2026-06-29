package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;

final class CacheModelConverter implements SyncConverter {
   public static final int VERSION_SUPPORTED = 1;
   private static ContextObjectWR _convertContextWR = new ContextObjectWR(19, 72);

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object == null) {
         return false;
      }

      CacheModel model = (CacheModel)object;
      ConversionProvider converter = model;
      SyncBuffer syncBuffer = new SyncBuffer(buffer, version, model.getUID());
      return converter.convert(_convertContextWR.getContextObject(), syncBuffer);
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version != 1) {
         return null;
      }

      SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);
      ContextObject convertContext = _convertContextWR.getContextObject();
      convertContext.put(255, syncBuffer);
      CacheModel model = (CacheModel)FactoryUtil.createInstance(1043003344163629438L, convertContext);
      convertContext.remove(255);
      return model;
   }
}
