package net.rim.device.apps.internal.smartcard.datakey;

import java.io.InputStream;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

class EntrustPKCS8_PrivateKeyDecoder {
   public static PrivateKey decodeKey(InputStream encodedKey) throws InvalidKeyEncodingException {
      if (encodedKey == null) {
         throw new IllegalArgumentException();
      }

      try {
         ASN1InputStream asn1Stream = new ASN1InputStream(encodedKey);
         ASN1InputStream privateKeyInfo = asn1Stream.readSequence();
         if (privateKeyInfo != null && privateKeyInfo.readInteger() == 0) {
            ASN1InputStream algorithmIdentifier = privateKeyInfo.readSequence();
            if (algorithmIdentifier == null) {
               throw new InvalidKeyEncodingException();
            } else {
               OID oid = algorithmIdentifier.readOID();
               String keyAlgorithm = OIDs.getAssociatedString(-5860934937401098689L, oid);
               if (keyAlgorithm == null) {
                  throw new NoSuchAlgorithmException();
               } else {
                  return decodeKey(algorithmIdentifier, privateKeyInfo, keyAlgorithm);
               }
            }
         } else {
            throw new InvalidKeyEncodingException();
         }
      } finally {
         throw new InvalidKeyEncodingException();
      }
   }

   private static PrivateKey decodeKey(ASN1InputStream parameters, ASN1InputStream privateKeyInfo, String algorithm) throws InvalidKeyEncodingException {
      if (!algorithm.equals("RSA")) {
         throw new IllegalArgumentException();
      }

      ASN1InputByteArray rsaPrivateKeySequence = new ASN1InputByteArray(privateKeyInfo.readOctetStringAsByteArray());
      rsaPrivateKeySequence.readSequence();
      if (rsaPrivateKeySequence == null) {
         throw new InvalidKeyEncodingException();
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
            RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(n.length * 8);
            return new RSAPrivateKey(rsaCryptoSystem, e, d, n, p, q, dModPm1, dModQm1, qInvModP);
         }
         case 127: {
            byte[] e = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] p = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] q = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] dModPm1 = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] dModQm1 = rsaPrivateKeySequence.readIntegerAsByteArray();
            byte[] qInvModP = rsaPrivateKeySequence.readIntegerAsByteArray();
            RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem((p.length + q.length) * 8);
            return new RSAPrivateKey(rsaCryptoSystem, e, p, q, dModPm1, dModQm1, qInvModP);
         }
         default:
            throw new InvalidKeyEncodingException("Unsupported PKCS8 version:" + version);
      }
   }
}
