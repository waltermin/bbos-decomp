package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.PrivateKey;

public class KeyStore_PrivateKeyDecoder extends PrivateKeyDecoder {
   @Override
   protected PrivateKey decodeKey(InputStream encodedKey, CryptoSystem cryptoSystem, String keyAlgorithm) {
      if (encodedKey == null) {
         throw new Object();
      }

      DataInputStream input = (DataInputStream)(new Object(encodedKey));
      if (input.readInt() != 0) {
         throw new InvalidKeyEncodingException();
      }

      String keyType = String.valueOf(input.readInt());
      if (keyType == null) {
         throw new InvalidKeyEncodingException();
      }

      KeyStore_PrivateKeyDecoder keyDecoder = (KeyStore_PrivateKeyDecoder)PrivateKeyDecoder.getDecoder("KeyStore", keyType);
      return keyDecoder.decodeKey(input, cryptoSystem, keyType);
   }

   protected PrivateKey decodeKey(DataInputStream input, CryptoSystem cryptoSystem, String algorithm) {
      throw new Object();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected String[] getKeyAlgorithms() {
      return null;
   }
}
