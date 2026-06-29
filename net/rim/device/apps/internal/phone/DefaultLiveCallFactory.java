package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.livecall.LiveCallFactory;

final class DefaultLiveCallFactory implements LiveCallFactory {
   static final long GUID;

   @Override
   public final Application getApplication() {
      return null;
   }

   @Override
   public final LiveCall createInstance(PhoneCallInitialData initialData, Object context) {
      PhoneCallInitialData data = null;
      LiveCall instance = null;
      if (initialData instanceof Object) {
         data = initialData;
      } else if (context instanceof Object) {
         data = (PhoneCallInitialData)ContextObject.get(context, -702275867225586088L);
      }

      if (data == null) {
         return new StandardCall((PhoneCallInitialData)(new Object(0, (byte)1, 0, null, null)), context);
      }

      if (PhoneUtilities.cdmaTypeNetwork()) {
         instance = new CDMACall(data, context);
      } else if (!PhoneUtilities.getPrivateFlag(context, 48)) {
         instance = new StandardCall(data, context);
      }

      return instance;
   }
}
