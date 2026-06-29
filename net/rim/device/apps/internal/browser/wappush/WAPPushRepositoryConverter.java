package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;

final class WAPPushRepositoryConverter implements SyncConverter {
   public static final int VERSION_SUPPORTED = 1;
   private static ContextObjectWR _convertContextWR = (ContextObjectWR)(new Object(19, 61));

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object == null) {
         return false;
      }

      WAPPushModel model = (WAPPushModel)object;
      ConversionProvider converter = model;
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(buffer, version, model.getUID()));
      return converter.convert(_convertContextWR.getContextObject(), syncBuffer);
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version != 1) {
         return null;
      }

      SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, version, uid));
      ContextObject convertContext = _convertContextWR.getContextObject();
      convertContext.put(255, syncBuffer);
      WAPPushModel model = (WAPPushModel)FactoryUtil.createInstance(-4153783271009930225L, convertContext);
      convertContext.remove(255);
      return model;
   }
}
