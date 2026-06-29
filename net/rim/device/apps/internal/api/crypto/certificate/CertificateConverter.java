package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

public class CertificateConverter extends BaseConverter {
   @Override
   public boolean canConvert(Object object) {
      if (!(object instanceof Object)) {
         if (object instanceof Object) {
            String contentType = (String)object;
            return this.isCertificateContentType(contentType);
         }
      } else {
         Parameters parameters = (Parameters)object;
         byte[] cmimeContentTypeBytes = parameters.getFirst((byte)1);
         String cmimeContentType = CMIMEContentType.getBaseType(cmimeContentTypeBytes);
         if (this.canConvert(cmimeContentType)) {
            return true;
         }

         if (cmimeContentType.equals("application/octet-stream")) {
            byte[] name = parameters.getFirst((byte)-14);
            if (name != null && name.length > 0) {
               return this.isCertificateFileName((String)(new Object(name)));
            }
         }
      }

      return false;
   }

   @Override
   public Object convert(byte[] inputBytes, Object inputObject) {
      if (inputBytes != null && inputObject instanceof Object) {
         Parameters parameters = (Parameters)inputObject;
         ContextObject context = (ContextObject)(new Object());
         context.put(8849067667159082262L, inputBytes);
         context.put(-7353832199068708928L, parameters);
         return this.createCertificateAttachmentModel(context);
      } else {
         return null;
      }
   }

   public boolean isCertificateContentType(String _1) {
      throw null;
   }

   public boolean isCertificateFileName(String _1) {
      throw null;
   }

   public Object createCertificateAttachmentModel(Object _1) {
      throw null;
   }
}
