package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.PrivateKey;

public class MSCAPI_PrivateKeyDecoder extends PrivateKeyDecoder {
   @Override
   protected PrivateKey decodeKey(InputStream input, CryptoSystem defaultCryptoSystem, String defaultKeyAlgorithm) {
      if (input == null) {
         throw new Object();
      }

      CryptoUtilities.verifyKeyBytes(input, 7, 2, 0, 0);
      int keyAlgorithm = CryptoUtilities.readIntegerLittleEndian(input);
      int type = (keyAlgorithm & 3584) >> 9;
      String algorithm = Integer.toString(type);
      MSCAPI_PrivateKeyDecoder keyDecoder = (MSCAPI_PrivateKeyDecoder)PrivateKeyDecoder.getDecoder("MSCAPI", algorithm);
      return keyDecoder.decodeKey(input, algorithm);
   }

   protected PrivateKey decodeKey(InputStream input, String algorithm) {
      throw new Object();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "MSCAPI";
   }

   @Override
   protected String[] getKeyAlgorithms() {
      return null;
   }
}
