package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

public final class CMIMETextPlainConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      if (parameters instanceof String) {
         String s = (String)parameters;
         if (StringUtilities.startsWithIgnoreCase(s, "text/plain", 1701707776)) {
            return true;
         }
      }

      if (!(parameters instanceof Parameters)) {
         return false;
      }

      Parameters cmimeParameters = (Parameters)parameters;
      String type = CMIMEContentType.getBaseType(cmimeParameters.getFirst((byte)1));
      return this.canConvert(type);
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      return CMIMEStringConverter.getInstance().convert(inputBytes, contextObject);
   }

   @Override
   public final byte[] convert(Object inputObject, Object contextObject) {
      return CMIMEStringConverter.getInstance().convert(inputObject, contextObject);
   }
}
