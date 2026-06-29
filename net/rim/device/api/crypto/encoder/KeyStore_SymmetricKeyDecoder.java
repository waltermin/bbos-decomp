package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.SymmetricKey;

public class KeyStore_SymmetricKeyDecoder extends SymmetricKeyDecoder {
   @Override
   protected SymmetricKey decodeKey(InputStream encodedKey, String keyAlgorithm) throws InvalidKeyEncodingException {
      if (encodedKey == null) {
         throw new IllegalArgumentException();
      }

      DataInputStream input = new DataInputStream(encodedKey);
      if (input.readInt() != 0) {
         throw new InvalidKeyEncodingException();
      }

      String keyType = String.valueOf(input.readInt());
      if (keyType == null) {
         throw new InvalidKeyEncodingException();
      }

      KeyStore_SymmetricKeyDecoder keyDecoder = (KeyStore_SymmetricKeyDecoder)SymmetricKeyDecoder.getDecoder("KeyStore", keyType);
      return keyDecoder.decodeKey(input, keyType);
   }

   protected SymmetricKey decodeKey(DataInputStream input, String algorithm) {
      throw new RuntimeException();
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
