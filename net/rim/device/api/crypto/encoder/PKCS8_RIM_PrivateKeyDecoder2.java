package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;

final class PKCS8_RIM_PrivateKeyDecoder2 extends PKCS8_PrivateKeyDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final PrivateKey decodeKey(ASN1InputStream parameters, ASN1InputStream privateKeyInfo, CryptoSystem cryptoSystem, String algorithm) {
      if (!algorithm.equals("DH")) {
         if (algorithm.equals("DSA")) {
            boolean var21 = false /* VF: Semaphore variable */;

            DSACryptoSystem _cryptoSystem;
            try {
               var21 = true;
               if (parameters.peekNextTag() == 16) {
                  ASN1InputStream params = parameters.readSequence();
                  byte[] p = params.readIntegerAsByteArray();
                  byte[] q = params.readIntegerAsByteArray();
                  byte[] g = params.readIntegerAsByteArray();
                  _cryptoSystem = (DSACryptoSystem)(new Object(p, q, g));
                  var21 = false;
               } else {
                  if (!(cryptoSystem instanceof Object)) {
                     throw new Object();
                  }

                  _cryptoSystem = (DSACryptoSystem)cryptoSystem;
                  var21 = false;
               }
            } finally {
               if (var21) {
                  label136: {
                     if (!(cryptoSystem instanceof Object)) {
                        throw new Object();
                     }

                     _cryptoSystem = (DSACryptoSystem)cryptoSystem;
                     break label136;
                  }
               }
            }

            ASN1InputByteArray in = new ASN1InputByteArray(privateKeyInfo.readOctetStringAsByteArray());
            byte[] keyData = in.readIntegerAsByteArray();
            return (PrivateKey)(new Object(_cryptoSystem, keyData));
         } else {
            if (!algorithm.equals("RSA")) {
               throw new Object();
            }

            ASN1InputByteArray rsaPrivateKeySequence = new ASN1InputByteArray(privateKeyInfo.readOctetStringAsByteArray());
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
      } else {
         DHCryptoSystem _cryptoSystem = null;
         boolean var18 = false /* VF: Semaphore variable */;

         try {
            var18 = true;
            if (parameters.peekNextTag() == 16) {
               ASN1InputStream params = parameters.readSequence();
               byte[] p = params.readIntegerAsByteArray();
               byte[] g = params.readIntegerAsByteArray();
               byte[] q = params.readIntegerAsByteArray();
               if (q.length == 1 && q[0] == 0) {
                  _cryptoSystem = (DHCryptoSystem)(new Object(p, g));
                  var18 = false;
               } else {
                  _cryptoSystem = (DHCryptoSystem)(new Object(p, q, g));
                  var18 = false;
               }
            } else {
               if (!(cryptoSystem instanceof Object)) {
                  throw new Object();
               }

               _cryptoSystem = (DHCryptoSystem)cryptoSystem;
               var18 = false;
            }
         } finally {
            if (var18) {
               label151: {
                  if (!(cryptoSystem instanceof Object)) {
                     throw new Object();
                  }

                  _cryptoSystem = (DHCryptoSystem)cryptoSystem;
                  break label151;
               }
            }
         }

         ASN1InputByteArray in = new ASN1InputByteArray(privateKeyInfo.readOctetStringAsByteArray());
         byte[] keyData = in.readIntegerAsByteArray();
         return (PrivateKey)(new Object(_cryptoSystem, keyData));
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"DH", "DSA", "RSA"};
   }
}
