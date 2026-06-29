package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPublicKey;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;

final class X509_RIM_PublicKeyDecoder2 extends X509_PublicKeyDecoder {
   @Override
   public final PublicKey decodeKey(ASN1InputByteArray parameters, String algorithm, byte[] subjectPublicKey, CryptoSystem cryptoSystem) throws InvalidCryptoSystemException, InvalidKeyEncodingException {
      try {
         if (algorithm.equals("DH")) {
            DHCryptoSystem _cryptoSystem;
            if (parameters.peekNextTag() == 16) {
               parameters.readSequence();
               byte[] p = parameters.readIntegerAsByteArray();
               byte[] g = parameters.readIntegerAsByteArray();
               byte[] q = parameters.readIntegerAsByteArray();
               if (q.length == 1 && q[0] == 0) {
                  _cryptoSystem = new DHCryptoSystem(p, g);
               } else {
                  _cryptoSystem = new DHCryptoSystem(p, q, g);
               }
            } else {
               if (!(cryptoSystem instanceof DHCryptoSystem)) {
                  throw new InvalidCryptoSystemException();
               }

               _cryptoSystem = (DHCryptoSystem)cryptoSystem;
            }

            ASN1InputByteArray keyStream = new ASN1InputByteArray(subjectPublicKey);
            byte[] y = keyStream.readIntegerAsByteArray();
            return new DHPublicKey(_cryptoSystem, y);
         }

         if (algorithm.equals("DSA")) {
            DSACryptoSystem _cryptoSystem;
            if (parameters.peekNextTag() == 16) {
               parameters.readSequence();
               byte[] p = parameters.readIntegerAsByteArray();
               byte[] q = parameters.readIntegerAsByteArray();
               byte[] g = parameters.readIntegerAsByteArray();
               _cryptoSystem = new DSACryptoSystem(p, q, g);
            } else {
               if (!(cryptoSystem instanceof DSACryptoSystem)) {
                  throw new InvalidCryptoSystemException();
               }

               _cryptoSystem = (DSACryptoSystem)cryptoSystem;
            }

            ASN1InputByteArray keyStream = new ASN1InputByteArray(subjectPublicKey);
            byte[] y = keyStream.readIntegerAsByteArray();
            return new DSAPublicKey(_cryptoSystem, y);
         }

         if (algorithm.equals("RSA")) {
            ASN1InputByteArray keyInfo = new ASN1InputByteArray(subjectPublicKey);
            keyInfo.readSequence();
            byte[] n = keyInfo.readIntegerAsByteArray();
            byte[] e = keyInfo.readIntegerAsByteArray();
            return new RSAPublicKey(new RSACryptoSystem(n.length * 8), e, n);
         }
      } catch (ASN1EncodingException var9) {
      }

      throw new InvalidKeyEncodingException();
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
