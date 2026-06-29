package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.InvalidSignatureEncodingException;

public class WTLS_SignatureDecoder extends SignatureDecoder {
   @Override
   protected DecodedSignature decodeSignature(InputStream encodedSignature, String signatureAlgorithm, String digestAlgorithm) throws InvalidSignatureEncodingException {
      if (encodedSignature == null) {
         throw new Object();
      }

      try {
         int length = encodedSignature.read() << 8 | encodedSignature.read() & 0xFF;
         byte[] signatureData = new byte[length];
         encodedSignature.read(signatureData, 0, length);
         WTLS_SignatureDecoder _decoder = (WTLS_SignatureDecoder)SignatureDecoder.getDecoder("WTLS", signatureAlgorithm);
         return _decoder.decodeSignature(signatureData, signatureAlgorithm, digestAlgorithm);
      } finally {
         throw new InvalidSignatureEncodingException();
      }
   }

   protected DecodedSignature decodeSignature(byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) {
      throw new Object();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "WTLS";
   }

   @Override
   protected String[] getSignatureAlgorithms() {
      return null;
   }
}
