package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.DSAPublicKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class X509_RIM_PublicKeyEncoder2 extends PublicKeyEncoder {
   @Override
   public final EncodedKey encodeKey(PublicKey key, long flags) {
      try {
         ASN1OutputStream asn1Stream = new ASN1OutputStream();
         ASN1OutputStream subjectPublicKeyInfo = new ASN1OutputStream();
         ASN1OutputStream algorithmIdentifier = new ASN1OutputStream();
         if (key.getAlgorithm().equals("DH")) {
            DHPublicKey _key = (DHPublicKey)key;
            OID algorithm = OIDs.getOID(-1487623704);
            algorithmIdentifier.writeOID(algorithm);
            if ((flags & 1) == 0) {
               ASN1OutputStream params = new ASN1OutputStream();
               params.writeInteger(_key.getDHCryptoSystem().getP());
               params.writeInteger(_key.getDHCryptoSystem().getG());
               byte[] q = _key.getDHCryptoSystem().getQ();
               if (q == null) {
                  params.writeInteger(0);
               } else {
                  params.writeInteger(q);
               }

               algorithmIdentifier.writeSequence(params);
            }

            subjectPublicKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream subjectPublicKey = new ASN1OutputStream();
            subjectPublicKey.writeInteger(_key.getPublicKeyData());
            subjectPublicKeyInfo.writeBitString(subjectPublicKey.toByteArray());
            asn1Stream.writeSequence(subjectPublicKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "X509"));
         }

         if (key.getAlgorithm().equals("DSA")) {
            DSAPublicKey _key = (DSAPublicKey)key;
            OID algorithm = OIDs.getOID(-1487364632);
            algorithmIdentifier.writeOID(algorithm);
            if ((flags & 1) == 0) {
               ASN1OutputStream params = new ASN1OutputStream();
               params.writeInteger(_key.getDSACryptoSystem().getP());
               params.writeInteger(_key.getDSACryptoSystem().getQ());
               params.writeInteger(_key.getDSACryptoSystem().getG());
               algorithmIdentifier.writeSequence(params);
            }

            subjectPublicKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream subjectPublicKey = new ASN1OutputStream();
            subjectPublicKey.writeInteger(_key.getPublicKeyData());
            subjectPublicKeyInfo.writeBitString(subjectPublicKey.toByteArray());
            asn1Stream.writeSequence(subjectPublicKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "X509"));
         }

         if (key.getAlgorithm().equals("RSA")) {
            RSAPublicKey _key = (RSAPublicKey)key;
            OID algorithm = OIDs.getOID(541853244);
            algorithmIdentifier.writeOID(algorithm);
            subjectPublicKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream subjectPublicKey = new ASN1OutputStream();
            subjectPublicKey.writeInteger(_key.getN());
            subjectPublicKey.writeInteger(_key.getE());
            ASN1OutputStream tempSequence = new ASN1OutputStream();
            tempSequence.writeSequence(subjectPublicKey);
            subjectPublicKeyInfo.writeBitString(tempSequence.toByteArray());
            asn1Stream.writeSequence(subjectPublicKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "X509"));
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"DH", "DSA", "RSA"};
   }
}
