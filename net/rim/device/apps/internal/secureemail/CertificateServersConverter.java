package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.CertificateOptionsSyncConverter;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

public class CertificateServersConverter extends BaseConverter {
   @Override
   public boolean canConvert(Object parameters) {
      if (!(parameters instanceof Parameters)) {
         return false;
      }

      Parameters cmimeParameters = (Parameters)parameters;
      String contentType = CMIMEContentType.getBaseType(cmimeParameters.getFirst((byte)1));
      return contentType.equals(SendCertificateServerVerb.TYPE_APPLICATION_CERTIFICATE_SERVERS);
   }

   @Override
   public Object convert(byte[] inputBytes, Object inputObject) {
      if (inputBytes != null && inputObject instanceof Parameters) {
         Parameters parameters = (Parameters)inputObject;
         ContextObject context = new ContextObject();
         context.put(8849067667159082262L, inputBytes);
         context.put(-7353832199068708928L, parameters);
         return new CertificateServersAttachmentModel(context);
      } else {
         return null;
      }
   }

   public static byte[] convert(CertificateServerInfo serverInfo) {
      if (serverInfo == null) {
         throw new IllegalArgumentException();
      }

      DataBuffer buffer = new DataBuffer();
      CertificateOptionsSyncConverter converter = new CertificateOptionsSyncConverter();
      return converter.convert(serverInfo, buffer, 0) ? buffer.toArray() : null;
   }

   public static CertificateServerInfo convert(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      DataBuffer buffer = new DataBuffer(data, 0, data.length, true);
      CertificateOptionsSyncConverter converter = new CertificateOptionsSyncConverter();

      try {
         return (CertificateServerInfo)converter.convert(buffer, 0, -1);
      } finally {
         ;
      }
   }
}
