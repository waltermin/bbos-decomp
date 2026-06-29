package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.ECPublicKey;
import net.rim.device.api.crypto.KEAPublicKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class X509_RIM_PublicKeyEncoder3 extends PublicKeyEncoder {
   @Override
   public final EncodedKey encodeKey(PublicKey key, long flags) {
      try {
         ASN1OutputStream asn1Stream = new ASN1OutputStream();
         ASN1OutputStream subjectPublicKeyInfo = new ASN1OutputStream();
         ASN1OutputStream algorithmIdentifier = new ASN1OutputStream();
         if (key.getAlgorithm().equals("EC")) {
            ECPublicKey _key = (ECPublicKey)key;
            OID algorithm = OIDs.getOID(-1487624216);
            algorithmIdentifier.writeOID(algorithm);
            if ((flags & 1) == 0) {
               OID curveParams = OIDs.getAssociatedOID(-3607261449824502613L, _key.getECCryptoSystem().getName());
               algorithmIdentifier.writeOID(curveParams);
            } else {
               algorithmIdentifier.writeNull();
            }

            subjectPublicKeyInfo.writeSequence(algorithmIdentifier);
            byte[] publicKeyData;
            if ((flags & 2) != 0) {
               publicKeyData = _key.getPublicKeyData(true);
            } else if ((flags & 4) != 0) {
               publicKeyData = _key.getPublicKeyData(false);
            } else {
               publicKeyData = _key.getPublicKeyData();
            }

            subjectPublicKeyInfo.writeBitString(publicKeyData);
            asn1Stream.writeSequence(subjectPublicKeyInfo);
            return new EncodedKey(asn1Stream.toByteArray(), "X509");
         }

         if (key.getAlgorithm().equals("KEA")) {
            KEAPublicKey _key = (KEAPublicKey)key;
            OID algorithm = OIDs.getOID(545973096);
            algorithmIdentifier.writeOID(algorithm);
            ASN1OutputStream params = new ASN1OutputStream();
            params.writeInteger(_key.getKEACryptoSystem().getP());
            params.writeInteger(_key.getKEACryptoSystem().getG());
            params.writeInteger(_key.getKEACryptoSystem().getQ());
            byte[] sequence = params.toByteArray();
            SHA1Digest digest = new SHA1Digest();
            digest.update(sequence);
            byte[] result = digest.getDigest();
            int half = digest.getDigestLength() >> 1;
            byte[] output = new byte[half];

            for (int i = 0; i < half; i++) {
               output[i] = (byte)(result[i] ^ result[i + half]);
            }

            algorithmIdentifier.writeOctetString(output);
            subjectPublicKeyInfo.writeSequence(algorithmIdentifier);
            subjectPublicKeyInfo.writeBitString(_key.getPublicKeyData());
            asn1Stream.writeSequence(subjectPublicKeyInfo);
            return new EncodedKey(asn1Stream.toByteArray(), "X509");
         }
      } finally {
         throw new IllegalArgumentException();
      }

      throw new IllegalArgumentException();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"EC", "KEA"};
   }
}
