package net.rim.device.apps.internal.smartcard.datakey;

import java.io.InputStream;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

class EntrustPKCS8_PrivateKeyDecoder {
   public static PrivateKey decodeKey(InputStream encodedKey) {
      if (encodedKey == null) {
         throw new Object();
      }

      try {
         ASN1InputStream asn1Stream = (ASN1InputStream)(new Object(encodedKey));
         ASN1InputStream privateKeyInfo = asn1Stream.readSequence();
         if (privateKeyInfo != null && privateKeyInfo.readInteger() == 0) {
            ASN1InputStream algorithmIdentifier = privateKeyInfo.readSequence();
            if (algorithmIdentifier == null) {
               throw new Object();
            } else {
               OID oid = algorithmIdentifier.readOID();
               String keyAlgorithm = OIDs.getAssociatedString(-5860934937401098689L, oid);
               if (keyAlgorithm == null) {
                  throw new Object();
               } else {
                  return decodeKey(algorithmIdentifier, privateKeyInfo, keyAlgorithm);
               }
            }
         } else {
            throw new Object();
         }
      } finally {
         throw new Object();
      }
   }

   private static PrivateKey decodeKey(ASN1InputStream parameters, ASN1InputStream privateKeyInfo, String algorithm) {
      if (!algorithm.equals("RSA")) {
         throw new Object();
      }

      ASN1InputByteArray rsaPrivateKeySequence = (ASN1InputByteArray)(new Object(privateKeyInfo.readOctetStringAsByteArray()));
      rsaPrivateKeySequence.readSequence();
      if (rsaPrivateKeySequence == null) {
         throw new Object();
      }

      int version = rsaPrivateKeySequence.readInteger();
      switch (version) {
         case 0: {
            byte[] n = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] e = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] d = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] p = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] q = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] dModPm1 = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] dModQm1 = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] qInvModP = rsaPrivateKeySequence.readIntegerAsByteArray();
            RSACryptoSystem rsaCryptoSystem = (RSACryptoSystem)(new Object(n.length * 8));
            return (PrivateKey)(new Object(rsaCryptoSystem, e, d, n, p, q, dModPm1, dModQm1, qInvModP));
         }
         case 127: {
            byte[] e = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] p = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] q = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] dModPm1 = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] dModQm1 = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] qInvModP = rsaPrivateKeySequence.readIntegerAsByteArray();
            RSACryptoSystem rsaCryptoSystem = (RSACryptoSystem)(new Object((p.length + q.length) * 8));
            return (PrivateKey)(new Object(rsaCryptoSystem, e, p, q, dModPm1, dModQm1, qInvModP));
         }
         default:
            throw new Object(((StringBuffer)(new Object("Unsupported PKCS8 version:"))).append(version).toString());
      }
   }
}
