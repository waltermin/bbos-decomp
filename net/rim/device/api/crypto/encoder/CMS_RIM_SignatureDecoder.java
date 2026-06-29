package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.InvalidSignatureEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;

final class CMS_RIM_SignatureDecoder extends CMS_SignatureDecoder {
   @Override
   protected final DecodedSignature decodeSignature(ASN1InputByteArray parameters, byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) throws InvalidSignatureEncodingException {
      try {
         if (digestAlgorithm == null) {
            digestAlgorithm = "SHA1";
         }

         Digest digest = DigestFactory.getInstance(digestAlgorithm);
         if (signatureAlgorithm.equals("ECDSA")) {
            ASN1InputByteArray asn1Stream = new ASN1InputByteArray(encodedSignature);
            asn1Stream.readSequence();
            byte[] _r = asn1Stream.readIntegerAsByteArray();
            byte[] _s = asn1Stream.readIntegerAsByteArray();
            return new ECDSADecodedSignature(_r, _s, digest);
         }

         if (signatureAlgorithm.equals("DSA")) {
            ASN1InputByteArray asn1Stream = new ASN1InputByteArray(encodedSignature);
            asn1Stream.readSequence();
            byte[] _r = asn1Stream.readIntegerAsByteArray();
            byte[] _s = asn1Stream.readIntegerAsByteArray();
            return new DSADecodedSignature(_r, _s, digest);
         }

         if (signatureAlgorithm.startsWith("RSA_PKCS1") || signatureAlgorithm.equals("RSA")) {
            byte[] _signature = Arrays.copy(encodedSignature);
            return new PKCS1DecodedSignature(_signature, digest);
         }

         if (signatureAlgorithm.equals("RSA_PSS")) {
            String hashName = "SHA1";
            if ((parameters.peekNextTag() & 31) == 16) {
               parameters.readSequence();
               parameters.readSequence();
               OID hashFunction = parameters.readOID();
               hashName = OIDs.getAssociatedString(3134008036018563479L, hashFunction);
            }

            if (hashName == null) {
               throw new InvalidSignatureEncodingException();
            }

            digest = DigestFactory.getInstance(hashName);
            byte[] _signature = Arrays.copy(encodedSignature);
            return new PSSDecodedSignature(_signature, digest);
         } else {
            throw new NoSuchAlgorithmException(signatureAlgorithm);
         }
      } finally {
         throw new InvalidSignatureEncodingException();
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "CMS";
   }

   @Override
   protected final String[] getSignatureAlgorithms() {
      return new String[]{"ECDSA", "DSA", "RSA", "RSA_PKCS1", "RSA_PKCS1_V15", "RSA_PKCS1_V20", "RSA_PSS"};
   }
}
