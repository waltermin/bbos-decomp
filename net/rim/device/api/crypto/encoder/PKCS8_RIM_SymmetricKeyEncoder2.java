package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OIDs;

final class PKCS8_RIM_SymmetricKeyEncoder2 extends SymmetricKeyEncoder {
   @Override
   protected final EncodedKey encodeKey(SymmetricKey key) {
      if (key.getAlgorithm().equals("AES")) {
         return encodeKey(key, 1536208863);
      } else if (key.getAlgorithm().equals("ARC4")) {
         return encodeKey(key, 1536208855);
      } else if (key.getAlgorithm().equals("DES")) {
         return encodeKey(key, 1536208879);
      } else if (key.getAlgorithm().equals("RC5")) {
         return encodeKey(key, 1536208903);
      } else if (key.getAlgorithm().equals("TripleDES")) {
         return encodeKey(key, 1536208919);
      } else if (key.getAlgorithm().equals("HMAC")) {
         return encodeKey(key, 1536208887);
      } else {
         throw new Object();
      }
   }

   static final EncodedKey encodeKey(SymmetricKey key, int oid) {
      try {
         ASN1OutputStream algorithmIdentifier = new ASN1OutputStream();
         algorithmIdentifier.writeOID(OIDs.getOID(oid));
         algorithmIdentifier.writeNull();
         ASN1OutputStream symmetricKeyInfo = new ASN1OutputStream();
         symmetricKeyInfo.writeInteger(0);
         symmetricKeyInfo.writeSequence(algorithmIdentifier);
         symmetricKeyInfo.writeOctetString(key.getData());
         ASN1OutputStream asn1Stream = new ASN1OutputStream();
         asn1Stream.writeSequence(symmetricKeyInfo);
         return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
      } finally {
         throw new Object();
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"AES", "ARC4", "DES", "HMAC", "RC5", "TripleDES"};
   }
}
