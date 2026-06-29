package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.PublicKey;

public class MSCAPI_PublicKeyDecoder extends PublicKeyDecoder {
   @Override
   protected PublicKey decodeKey(InputStream input, CryptoSystem defaultCryptoSystem, String defaultKeyAlgorithm) {
      if (input == null) {
         throw new Object();
      }

      CryptoUtilities.verifyKeyBytes(input, 6, 2, 0, 0);
      int keyAlgorithm = CryptoUtilities.readIntegerLittleEndian(input);
      int type = (keyAlgorithm & 3584) >> 9;
      String algorithm = Integer.toString(type);
      MSCAPI_PublicKeyDecoder keyDecoder = (MSCAPI_PublicKeyDecoder)PublicKeyDecoder.getDecoder("MSCAPI", algorithm);
      return keyDecoder.decodeKey(input, algorithm);
   }

   protected PublicKey decodeKey(InputStream input, String algorithm) {
      throw new Object();
   }

   @Override
   public String getEncodingAlgorithm() {
      return "MSCAPI";
   }

   @Override
   public String[] getKeyAlgorithms() {
      return null;
   }
}
