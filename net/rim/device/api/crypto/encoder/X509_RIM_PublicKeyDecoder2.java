package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;

final class X509_RIM_PublicKeyDecoder2 extends X509_PublicKeyDecoder {
   @Override
   public final PublicKey decodeKey(ASN1InputByteArray parameters, String algorithm, byte[] subjectPublicKey, CryptoSystem cryptoSystem) {
      try {
         if (algorithm.equals("DH")) {
            DHCryptoSystem _cryptoSystem;
            if (parameters.peekNextTag() == 16) {
               parameters.readSequence();
               byte[] p = parameters.readIntegerAsByteArray();
               byte[] g = parameters.readIntegerAsByteArray();
               byte[] q = parameters.readIntegerAsByteArray();
               if (q.length == 1 && q[0] == 0) {
                  _cryptoSystem = (DHCryptoSystem)(new Object(p, g));
               } else {
                  _cryptoSystem = (DHCryptoSystem)(new Object(p, q, g));
               }
            } else {
               if (!(cryptoSystem instanceof Object)) {
                  throw new Object();
               }

               _cryptoSystem = (DHCryptoSystem)cryptoSystem;
            }

            ASN1InputByteArray keyStream = new ASN1InputByteArray(subjectPublicKey);
            byte[] y = keyStream.readIntegerAsByteArray();
            return (PublicKey)(new Object(_cryptoSystem, y));
         }

         if (algorithm.equals("DSA")) {
            DSACryptoSystem _cryptoSystem;
            if (parameters.peekNextTag() == 16) {
               parameters.readSequence();
               byte[] p = parameters.readIntegerAsByteArray();
               byte[] q = parameters.readIntegerAsByteArray();
               byte[] g = parameters.readIntegerAsByteArray();
               _cryptoSystem = (DSACryptoSystem)(new Object(p, q, g));
            } else {
               if (!(cryptoSystem instanceof Object)) {
                  throw new Object();
               }

               _cryptoSystem = (DSACryptoSystem)cryptoSystem;
            }

            ASN1InputByteArray keyStream = new ASN1InputByteArray(subjectPublicKey);
            byte[] y = keyStream.readIntegerAsByteArray();
            return (PublicKey)(new Object(_cryptoSystem, y));
         }

         if (algorithm.equals("RSA")) {
            ASN1InputByteArray keyInfo = new ASN1InputByteArray(subjectPublicKey);
            keyInfo.readSequence();
            byte[] n = keyInfo.readIntegerAsByteArray();
            byte[] e = keyInfo.readIntegerAsByteArray();
            return (PublicKey)(new Object((RSACryptoSystem)(new Object(n.length * 8)), e, n));
         }
      } catch (ASN1EncodingException var9) {
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
