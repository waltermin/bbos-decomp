package net.rim.device.apps.internal.browser.store;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;

final class BrowserRepositoryConverter implements SyncConverter {
   public static final int VERSION_SUPPORTED;
   private static ContextObjectWR _convertContextWR = (ContextObjectWR)(new Object(19, 61));

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object == null) {
         return false;
      }

      BrowserPageModel model = (BrowserPageModel)object;
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
      BrowserPageModel model = (BrowserPageModel)FactoryUtil.createInstance(8419621845400492256L, convertContext);
      convertContext.remove(255);
      return model;
   }
}
