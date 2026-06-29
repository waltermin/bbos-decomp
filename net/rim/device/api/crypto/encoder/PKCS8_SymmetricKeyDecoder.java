package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class PKCS8_SymmetricKeyDecoder extends SymmetricKeyDecoder {
   @Override
   protected SymmetricKey decodeKey(InputStream encodedKey, String keyAlgorithm) throws InvalidKeyEncodingException, NoSuchAlgorithmException {
      if (encodedKey == null) {
         throw new IllegalArgumentException();
      }

      try {
         ASN1InputStream asn1Stream = new ASN1InputStream(encodedKey);
         ASN1InputStream SymmetricKeyInfo = asn1Stream.readSequence();
         if (SymmetricKeyInfo != null && SymmetricKeyInfo.readInteger() == 0) {
            ASN1InputStream algorithmIdentifier = SymmetricKeyInfo.readSequence();
            if (algorithmIdentifier == null) {
               throw new InvalidKeyEncodingException();
            }

            OID oid = algorithmIdentifier.readOID();
            String _keyAlgorithm = OIDs.getAssociatedString(-5860934937401098689L, oid);
            if (_keyAlgorithm == null) {
               throw new NoSuchAlgorithmException(keyAlgorithm);
            }

            PKCS8_SymmetricKeyDecoder keyDecoder = (PKCS8_SymmetricKeyDecoder)SymmetricKeyDecoder.getDecoder("PKCS8", _keyAlgorithm);
            return keyDecoder.decodeKey(algorithmIdentifier, SymmetricKeyInfo, _keyAlgorithm);
         } else {
            throw new InvalidKeyEncodingException();
         }
      } catch (ASN1EncodingException e) {
         throw new InvalidKeyEncodingException();
      }
   }

   protected SymmetricKey decodeKey(ASN1InputStream parameters, ASN1InputStream symmetricKeyInfo, String algorithm) {
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
