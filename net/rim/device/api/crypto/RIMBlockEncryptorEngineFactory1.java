package net.rim.device.api.crypto;

import java.io.OutputStream;

final class RIMBlockEncryptorEngineFactory1 extends EncryptorFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"AES", "CBC", "DES", "ECB", "", "RC5", "TripleDES", "RSA", "PKCS1"};
   }

   @Override
   protected final Object create(String algorithm, String nextAlgorithm, Key key, OutputStream stream, InitializationVector iv) {
      String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(algorithm);
      if (baseAlgorithm.equals("AES")) {
         int blockBitLength = RIMFactoryUtilities.getBlockBitLength(algorithm, 128);
         return new AESEncryptorEngine((AESKey)key, blockBitLength >> 3);
      }

      if (baseAlgorithm.equals("DES")) {
         return new DESEncryptorEngine((DESKey)key);
      }

      if (baseAlgorithm.equals("TripleDES")) {
         return new TripleDESEncryptorEngine((TripleDESKey)key);
      }

      if (algorithm.equals("CBC")) {
         if (nextAlgorithm != null && iv != null) {
            return new CBCEncryptorEngine(EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null), iv);
         } else {
            throw new Object();
         }
      } else if (baseAlgorithm.equals("RC5")) {
         int blockBitLength = RIMFactoryUtilities.getBlockBitLength(algorithm, 64);
         int numRounds = RIMFactoryUtilities.getNumRounds(algorithm, 16);
         return new RC5EncryptorEngine((RC5Key)key, blockBitLength >> 3, numRounds);
      } else if (algorithm.equals("ECB") || algorithm.length() == 0) {
         return EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null);
      } else if (algorithm.equals("RSA")) {
         return new RSAEncryptorEngine((RSAPublicKey)key);
      } else if (algorithm.equals("PKCS1")) {
         return new PKCS1FormatterEngine((PublicKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null));
      } else {
         throw new Object();
      }
   }
}
