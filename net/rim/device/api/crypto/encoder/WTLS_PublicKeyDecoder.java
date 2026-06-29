package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.PublicKey;

public class WTLS_PublicKeyDecoder extends PublicKeyDecoder {
   @Override
   public PublicKey decodeKey(InputStream encodedKey, CryptoSystem cryptoSystem, String keyAlgorithm) {
      if (encodedKey == null) {
         throw new IllegalArgumentException();
      }

      String publicKeyType = String.valueOf(encodedKey.read() & 0xFF);
      PublicKeyDecoder keyDecoder = PublicKeyDecoder.getDecoder("WTLS", publicKeyType);
      return keyDecoder.decodeKey(encodedKey, cryptoSystem, publicKeyType);
   }

   @Override
   public String getEncodingAlgorithm() {
      return "WTLS";
   }

   @Override
   public String[] getKeyAlgorithms() {
      return null;
   }
}
