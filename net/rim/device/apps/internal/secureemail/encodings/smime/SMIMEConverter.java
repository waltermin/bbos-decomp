package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.secureemail.SecureEmailProcessor;

public final class SMIMEConverter extends BaseConverter {
   private static SMIMEConverter _instance;

   public static final synchronized SMIMEConverter getInstance() {
      if (_instance == null) {
         _instance = new SMIMEConverter();
      }

      return _instance;
   }

   @Override
   public final boolean canConvert(Object parameters) {
      if (parameters instanceof Parameters) {
         Parameters cmimeParameters = (Parameters)parameters;
         byte[] securityEncoding = cmimeParameters.getFirst((byte)-11);
         if (securityEncoding == null) {
            return false;
         }

         int securityEncodingBaseLength = Arrays.getIndex(securityEncoding, (byte)59);
         if (securityEncodingBaseLength == -1) {
            securityEncodingBaseLength = securityEncoding.length;
         }

         if (securityEncodingBaseLength == SMIMEConstants.SECURITY_ENCODING_SMIME_BYTES.length
            && Arrays.equals(securityEncoding, 0, SMIMEConstants.SECURITY_ENCODING_SMIME_BYTES, 0, securityEncodingBaseLength)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Object convert(byte[] inputBytes, Object parametersObject) {
      SMIMEBodyModel model = null;
      Parameters parameters = (Parameters)parametersObject;
      ContextObject context = new ContextObject();
      context.put(-7353832199068708928L, parameters);
      if (inputBytes == null) {
         byte[] secureEmailBodyData = parameters.getFirst((byte)-9);
         if (secureEmailBodyData == null) {
            return null;
         }

         byte[] secureEmailFlagsData = parameters.getFirst((byte)-8);
         int secureEmailFlags = secureEmailFlagsData != null ? CMIMEUtilities.decodeInteger(secureEmailFlagsData) : 0;
         boolean isSignedReceipt = (secureEmailFlags & 1) != 0;
         boolean isSignedReceiptRequest = (secureEmailFlags & 2) != 0;
         boolean isStoredAsBase64 = (secureEmailFlags & 4) != 0;
         model = new SMIMEBodyModel(secureEmailBodyData, isSignedReceipt, isSignedReceiptRequest, isStoredAsBase64, context);
      } else {
         model = new SMIMEBodyModel(inputBytes, context);
      }

      SecureEmailProcessor processor = SMIMEFactory.getInstance().createProcessor(model, null, false, context);
      processor.run(true);
      return model;
   }

   @Override
   public final byte[] convert(Object inputObject, Object contextObject) {
      String rawData = (String)inputObject;
      return rawData.getBytes();
   }
}
