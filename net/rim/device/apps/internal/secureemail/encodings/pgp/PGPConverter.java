package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.secureemail.SecureEmailProcessor;

public final class PGPConverter extends BaseConverter {
   private static PGPConverter _instance;

   @Override
   public final boolean canConvert(Object parameters) {
      if (parameters instanceof Object) {
         Parameters cmimeParameters = (Parameters)parameters;
         byte[] securityEncoding = cmimeParameters.getFirst((byte)-11);
         if (securityEncoding == null) {
            return false;
         }

         int securityEncodingBaseLength = Arrays.getIndex(securityEncoding, (byte)59);
         if (securityEncodingBaseLength == -1) {
            securityEncodingBaseLength = securityEncoding.length;
         }

         if (securityEncodingBaseLength == PGPConstants.SECURITY_ENCODING_PGP_BYTES.length
            && Arrays.equals(securityEncoding, 0, PGPConstants.SECURITY_ENCODING_PGP_BYTES, 0, securityEncodingBaseLength)) {
            return true;
         }
      }

      return false;
   }

   public static final synchronized PGPConverter getInstance() {
      if (_instance == null) {
         _instance = new PGPConverter();
      }

      return _instance;
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      PGPBodyModel model = null;
      Parameters parameters = (Parameters)contextObject;
      if (inputBytes == null && parameters != null) {
         inputBytes = parameters.getFirst((byte)-9);
      }

      if (inputBytes != null) {
         ContextObject context = (ContextObject)(new Object());
         context.put(-7353832199068708928L, parameters);
         model = new PGPBodyModel(inputBytes, context);
         SecureEmailProcessor processor = PGPFactory.getInstance().createProcessor(model, null, false, context);
         processor.run(true);
      }

      return model;
   }

   @Override
   public final byte[] convert(Object inputObject, Object contextObject) {
      String rawData = (String)inputObject;
      return rawData.getBytes();
   }
}
