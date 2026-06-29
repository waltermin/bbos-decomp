package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class PKCS8_PrivateKeyDecoder extends PrivateKeyDecoder {
   @Override
   protected PrivateKey decodeKey(InputStream encodedKey, CryptoSystem cryptoSystem, String keyAlgorithm) throws InvalidKeyEncodingException, NoSuchAlgorithmException {
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
            }

            OID oid = algorithmIdentifier.readOID();
            String _keyAlgorithm = OIDs.getAssociatedString(-5860934937401098689L, oid);
            if (_keyAlgorithm == null) {
               throw new NoSuchAlgorithmException(keyAlgorithm);
            }

            PKCS8_PrivateKeyDecoder keyDecoder = (PKCS8_PrivateKeyDecoder)PrivateKeyDecoder.getDecoder("PKCS8", _keyAlgorithm);
            return keyDecoder.decodeKey(algorithmIdentifier, privateKeyInfo, cryptoSystem, _keyAlgorithm);
         } else {
            throw new InvalidKeyEncodingException();
         }
      } catch (ASN1EncodingException e) {
         throw new InvalidKeyEncodingException();
      }
   }

   protected PrivateKey decodeKey(ASN1InputStream parameters, ASN1InputStream privateKeyInfo, CryptoSystem cryptoSystem, String algorithm) {
      throw new RuntimeException();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected String[] getKeyAlgorithms() {
      return null;
   }
}
