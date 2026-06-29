package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.InvalidSignatureEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.util.Arrays;

final class X509_RIM_SignatureDecoder2 extends X509_SignatureDecoder {
   @Override
   protected final DecodedSignature decodeSignature(ASN1InputByteArray parameters, byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) throws InvalidSignatureEncodingException, NoSuchAlgorithmException {
      try {
         if (digestAlgorithm == null) {
            digestAlgorithm = "SHA1";
         }

         Digest digest = DigestFactory.getInstance(digestAlgorithm);
         if (signatureAlgorithm.equals("DSA")) {
            ASN1InputByteArray asn1Stream = new ASN1InputByteArray(encodedSignature);
            asn1Stream.readSequence();
            byte[] _r = asn1Stream.readIntegerAsByteArray();
            byte[] _s = asn1Stream.readIntegerAsByteArray();
            return new DSADecodedSignature(_r, _s, digest);
         }

         if (signatureAlgorithm.startsWith("RSA")) {
            byte[] _signature = Arrays.copy(encodedSignature);
            return new PKCS1DecodedSignature(_signature, digest);
         }
      } catch (ASN1EncodingException e) {
         throw new InvalidSignatureEncodingException();
      }

      throw new NoSuchAlgorithmException(signatureAlgorithm);
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected final String[] getSignatureAlgorithms() {
      return new String[]{"DSA", "RSA", "RSA_PKCS1", "RSA_PKCS1_V15", "RSA_PKCS1_V20"};
   }
}
