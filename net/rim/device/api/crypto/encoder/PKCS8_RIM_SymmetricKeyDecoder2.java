package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.ARC4Key;
import net.rim.device.api.crypto.DESKey;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.RC5Key;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputStream;

final class PKCS8_RIM_SymmetricKeyDecoder2 extends PKCS8_SymmetricKeyDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SymmetricKey decodeKey(ASN1InputStream parameters, ASN1InputStream symmetricKeyInfo, String algorithm) throws InvalidKeyEncodingException {
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         if (algorithm.equals("AES")) {
            byte[] keyData = symmetricKeyInfo.readOctetStringAsByteArray();
            return new AESKey(keyData, 0, keyData.length * 8);
         }

         if (algorithm.equals("ARC4")) {
            byte[] keyData = symmetricKeyInfo.readOctetStringAsByteArray();
            return new ARC4Key(keyData, 0, keyData.length);
         }

         if (algorithm.equals("DES")) {
            byte[] keyData = symmetricKeyInfo.readOctetStringAsByteArray();
            return new DESKey(keyData, 0);
         }

         if (algorithm.equals("RC5")) {
            byte[] keyData = symmetricKeyInfo.readOctetStringAsByteArray();
            return new RC5Key(keyData, 0, keyData.length * 8);
         }

         if (algorithm.equals("TripleDES")) {
            byte[] keyData = symmetricKeyInfo.readOctetStringAsByteArray();
            return new TripleDESKey(keyData, 0);
         }

         if (algorithm.equals("HMAC")) {
            byte[] keyData = symmetricKeyInfo.readOctetStringAsByteArray();
            return new HMACKey(keyData, 0, keyData.length);
         }

         var7 = false;
      } catch (ASN1EncodingException var8) {
         var7 = false;
         throw new InvalidKeyEncodingException();
      } finally {
         if (var7) {
            throw new InvalidKeyEncodingException();
         }
      }

      throw new IllegalArgumentException();
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
