package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.ECCryptoSystem;
import net.rim.device.api.crypto.ECPrivateKey;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.KEACryptoSystem;
import net.rim.device.api.crypto.KEAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class PKCS8_RIM_PrivateKeyDecoder3 extends PKCS8_PrivateKeyDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final PrivateKey decodeKey(ASN1InputStream parameters, ASN1InputStream privateKeyInfo, CryptoSystem cryptoSystem, String algorithm) throws CryptoUnsupportedOperationException, InvalidCryptoSystemException, InvalidKeyEncodingException {
      if (!algorithm.equals("EC")) {
         if (algorithm.equals("KEA")) {
            KEACryptoSystem _cryptoSystem = null;
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               if (parameters.peekNextTag() == 16) {
                  ASN1InputStream params = parameters.readSequence();
                  byte[] p = params.readIntegerAsByteArray();
                  byte[] q = params.readIntegerAsByteArray();
                  byte[] g = params.readIntegerAsByteArray();
                  _cryptoSystem = new KEACryptoSystem(p, q, g);
                  var12 = false;
               } else {
                  if (!(cryptoSystem instanceof KEACryptoSystem)) {
                     throw new InvalidCryptoSystemException();
                  }

                  _cryptoSystem = (KEACryptoSystem)cryptoSystem;
                  var12 = false;
               }
            } finally {
               if (var12) {
                  label97: {
                     if (!(cryptoSystem instanceof KEACryptoSystem)) {
                        throw new InvalidCryptoSystemException();
                     }

                     _cryptoSystem = (KEACryptoSystem)cryptoSystem;
                     break label97;
                  }
               }
            }

            ASN1InputByteArray in = new ASN1InputByteArray(privateKeyInfo.readOctetStringAsByteArray());
            byte[] keyData = in.readIntegerAsByteArray();
            return new KEAPrivateKey(_cryptoSystem, keyData);
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         ECCryptoSystem _cryptoSystem = null;
         int nextTag = parameters.peekNextTag();
         if (nextTag == 6) {
            OID curveParam = parameters.readOID();
            String curveName = OIDs.getAssociatedString(-3607261449824502613L, curveParam);
            if (curveName == null) {
               throw new InvalidCryptoSystemException();
            }

            _cryptoSystem = new ECCryptoSystem(curveName);
         } else {
            if (nextTag != 5) {
               throw new CryptoUnsupportedOperationException();
            }

            if (!(cryptoSystem instanceof ECCryptoSystem)) {
               throw new InvalidCryptoSystemException();
            }

            _cryptoSystem = (ECCryptoSystem)cryptoSystem;
         }

         ASN1InputByteArray asn1Stream = new ASN1InputByteArray(privateKeyInfo.readOctetStringAsByteArray());
         asn1Stream.readSequence();
         if (asn1Stream != null && asn1Stream.readInteger() == 1) {
            byte[] keyData = asn1Stream.readOctetString();
            ECPrivateKey privateKey = new ECPrivateKey(_cryptoSystem, keyData);
            int tag = asn1Stream.peekNextTag();
            if (tag == 16 || tag == 6 || tag == 5) {
               asn1Stream.skipField();
            }

            if (asn1Stream.peekNextTag() == 3) {
               asn1Stream.skipField();
            }

            return privateKey;
         } else {
            throw new InvalidKeyEncodingException();
         }
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"EC", "KEA"};
   }
}
