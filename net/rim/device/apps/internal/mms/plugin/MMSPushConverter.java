package net.rim.device.apps.internal.mms.plugin;

import java.io.DataInput;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;

final class MMSPushConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) {
      if (aDataInput == null) {
         return null;
      }

      if (validateReceptionMode()) {
         MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(aDataInput);
         return new ConverterRunnable(pdu);
      }

      if (aDataInput instanceof Object) {
         try {
            System.out.println("MMS Notification refused - USERDCR.");
            PushInputStream pushStream = (PushInputStream)aDataInput;
            pushStream.decline(237);
            return null;
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   private static final boolean validateReceptionMode() {
      switch (MMSOptions.getInstance().getReceptionMode()) {
         case 1:
         default:
            return false;
         case 2:
            int networkService = RadioInfo.getNetworkService();
            if ((networkService & 8) != 0) {
               return false;
            }
         case 0:
            return true;
      }
   }
}
