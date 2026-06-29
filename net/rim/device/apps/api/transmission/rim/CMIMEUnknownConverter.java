package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

public final class CMIMEUnknownConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      Parameters parameters = (Parameters)contextObject;
      ContextObject context = (ContextObject)(new Object());
      if (inputBytes != null) {
         context.put(8849067667159082262L, inputBytes);
      }

      context.put(-7353832199068708928L, parameters);
      return FactoryUtil.createInstance(-1388261346431947322L, context);
   }
}
