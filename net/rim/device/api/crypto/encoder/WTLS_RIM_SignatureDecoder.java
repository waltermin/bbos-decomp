package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;

final class WTLS_RIM_SignatureDecoder extends WTLS_SignatureDecoder {
   @Override
   protected final DecodedSignature decodeSignature(byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) {
      switch (signatureAlgorithm.charAt(0)) {
         case '2':
            return new PKCS1DecodedSignature(encodedSignature, (Digest)(new Object()));
         default:
            throw new Object(signatureAlgorithm);
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "WTLS";
   }

   @Override
   protected final String[] getSignatureAlgorithms() {
      return new String[]{"2"};
   }
}
