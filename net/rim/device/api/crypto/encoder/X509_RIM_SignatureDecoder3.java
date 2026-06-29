package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;

final class X509_RIM_SignatureDecoder3 extends X509_SignatureDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final DecodedSignature decodeSignature(ASN1InputByteArray parameters, byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) {
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         if (signatureAlgorithm.equals("ECDSA")) {
            if (digestAlgorithm == null) {
               digestAlgorithm = "SHA1";
            }

            Digest digest = DigestFactory.getInstance(digestAlgorithm);
            ASN1InputByteArray asn1Stream = (ASN1InputByteArray)(new Object(encodedSignature));
            asn1Stream.readSequence();
            byte[] _r = asn1Stream.readIntegerAsByteArray();
            byte[] _s = asn1Stream.readIntegerAsByteArray();
            return new ECDSADecodedSignature(_r, _s, digest);
         }

         if (signatureAlgorithm.startsWith("RSA_PSS")) {
            String hashName = "SHA1";
            if ((parameters.peekNextTag() & 31) == 16) {
               parameters.readSequence();
               parameters.readSequence();
               OID hashFunction = parameters.readOID();
               hashName = OIDs.getAssociatedString(3134008036018563479L, hashFunction);
            }

            if (hashName == null) {
               throw new Object();
            }

            Digest digest = DigestFactory.getInstance(hashName);
            byte[] _signature = Arrays.copy(encodedSignature);
            return new PSSDecodedSignature(_signature, digest);
         }

         var10 = false;
      } finally {
         if (var10) {
            throw new Object();
         }
      }

      throw new Object(signatureAlgorithm);
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected final String[] getSignatureAlgorithms() {
      return new String[]{"ECDSA", "RSA_PSS"};
   }
}
