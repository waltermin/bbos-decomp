package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.Converter;

public class WLANProfileMessageAttachmentConverter extends BaseConverter {
   private static long CMIME_WLAN_PROFILE_CONVERTER_SINGLETON = 2963105472873618463L;

   public static void register() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Converter wlanProfileConverter = (Converter)ar.getOrWaitFor(CMIME_WLAN_PROFILE_CONVERTER_SINGLETON);
      if (wlanProfileConverter == null) {
         wlanProfileConverter = new WLANProfileMessageAttachmentConverter();
         ar.put(CMIME_WLAN_PROFILE_CONVERTER_SINGLETON, wlanProfileConverter);
      }

      CMIMEConverterRegistry.addConverter(wlanProfileConverter, 3);
   }

   @Override
   public boolean canConvert(Object parameters) {
      if (parameters instanceof Object) {
         return this.canConvertBasedOnMimeType((String)parameters);
      }

      if (parameters instanceof Object) {
         Parameters cmimeParameters = (Parameters)parameters;
         String type = CMIMEContentType.getBaseType(cmimeParameters.getFirst((byte)1));
         if (this.canConvertBasedOnMimeType(type)) {
            return true;
         }

         if ("application/octet-stream".equals(type)) {
            byte[] name = cmimeParameters.getFirst((byte)-14);
            if (name != null && name.length > 0) {
               return WLANProfileMessageAttachmentModel.isProfileFileName((String)(new Object(name)));
            }
         }
      }

      return false;
   }

   private boolean canConvertBasedOnMimeType(String mimeType) {
      return getMimeType().equalsIgnoreCase(mimeType);
   }

   public static String getMimeType() {
      return "application/vnd.wap.connectivity-wbxml";
   }

   @Override
   public Object convert(byte[] inputBytes, Object contextObject) {
      if (inputBytes != null && contextObject instanceof Object) {
         Parameters parameters = (Parameters)contextObject;
         ContextObject context = (ContextObject)(new Object());
         context.put(8849067667159082262L, inputBytes);
         context.put(-7353832199068708928L, parameters);
         return new WLANProfileMessageAttachmentModel(context);
      } else {
         return null;
      }
   }
}
