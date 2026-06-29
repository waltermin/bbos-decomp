package net.rim.device.apps.internal.attachment;

import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

public class AttachmentViewerModelConverter extends BaseConverter {
   @Override
   public boolean canConvert(Object parameters) {
      if (parameters instanceof Parameters) {
         Parameters cmimeParameters = (Parameters)parameters;
         byte[] contentBytes = cmimeParameters.getFirst((byte)-16);
         if (contentBytes != null) {
            CMIMEUtilities.decodeInteger(contentBytes);
         }

         byte[][] conversionsAvailable = cmimeParameters.get((byte)-10);
         if (conversionsAvailable != null) {
            int preferredConvertersion = AttachmentViewerModel.getPreferredConversion(conversionsAvailable);
            if (preferredConvertersion >= 0) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public Object convert(byte[] inputBytes, Object contextObject) {
      Parameters parameters = (Parameters)contextObject;
      ContextObject context = new ContextObject();
      if (inputBytes != null) {
         context.put(8849067667159082262L, inputBytes);
      }

      context.put(-7353832199068708928L, parameters);
      return FactoryUtil.createInstance(-811824568997914181L, context);
   }
}
