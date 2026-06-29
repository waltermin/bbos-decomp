package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.SHA1Digest;

final class WTLS_RIM_SignatureDecoder extends WTLS_SignatureDecoder {
   @Override
   protected final DecodedSignature decodeSignature(byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) throws NoSuchAlgorithmException {
      switch (signatureAlgorithm.charAt(0)) {
         case '2':
            return new PKCS1DecodedSignature(encodedSignature, new SHA1Digest());
         default:
            throw new NoSuchAlgorithmException(signatureAlgorithm);
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
