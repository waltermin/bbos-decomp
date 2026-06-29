package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class X509_PublicKeyDecoder extends PublicKeyDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected PublicKey decodeKey(InputStream encodedKey, CryptoSystem cryptoSystem, String keyAlgorithm) {
      if (encodedKey == null) {
         throw new Object();
      }

      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         ASN1InputStream e = new ASN1InputStream(encodedKey);
         ASN1InputByteArray realStream = new ASN1InputByteArray(e.readFieldAsByteArray());
         realStream.readSequence();
         int startOffset = realStream.getStartPosition();
         int endOffset = realStream.getEndPosition();
         realStream.skipField();
         byte[] keyMaterial = realStream.readBitString().toByteArray();
         realStream.setStartPosition(startOffset);
         realStream.setEndPosition(endOffset);
         realStream.readSequence();
         OID algorithm = realStream.readOID();
         String _keyAlgorithm = OIDs.getAssociatedString(-5860934937401098689L, algorithm);
         if (_keyAlgorithm == null) {
            _keyAlgorithm = keyAlgorithm;
         }

         if (_keyAlgorithm == null) {
            throw new Object();
         }

         X509_PublicKeyDecoder keyDecoder = (X509_PublicKeyDecoder)PublicKeyDecoder.getDecoder("X509", _keyAlgorithm);
         return keyDecoder.decodeKey(realStream, _keyAlgorithm, keyMaterial, cryptoSystem);
      } catch (ASN1EncodingException var15) {
         var14 = false;
      } finally {
         if (var14) {
            throw new Object();
         }
      }

      throw new Object();
   }

   protected PublicKey decodeKey(ASN1InputByteArray parameters, String algorithm, byte[] subjectPublicKey, CryptoSystem cryptoSystem) {
      throw new Object();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected String[] getKeyAlgorithms() {
      return null;
   }
}
