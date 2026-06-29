package net.rim.device.api.crypto;

import java.io.InputStream;

class RIMBlockDecryptorEngineFactory1 extends DecryptorFactory {
   @Override
   protected String[] getFactoryAlgorithms() {
      return new String[]{"AES", "CBC", "DES", "ECB", "", "RC5", "TripleDES", "RSA", "PKCS1"};
   }

   @Override
   protected Object create(String algorithm, String nextAlgorithm, Key key, InputStream stream, InitializationVector iv) {
      String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(algorithm);
      if (baseAlgorithm.equals("AES")) {
         int blockBitLength = RIMFactoryUtilities.getBlockBitLength(algorithm, 128);
         return new AESDecryptorEngine((AESKey)key, blockBitLength >> 3);
      }

      if (baseAlgorithm.equals("DES")) {
         return new DESDecryptorEngine((DESKey)key);
      }

      if (baseAlgorithm.equals("TripleDES")) {
         return new TripleDESDecryptorEngine((TripleDESKey)key);
      }

      if (algorithm.equals("CBC")) {
         if (nextAlgorithm != null && iv != null) {
            return new CBCDecryptorEngine(DecryptorFactory.getBlockDecryptorEngine(key, nextAlgorithm, null), iv);
         } else {
            throw new IllegalArgumentException();
         }
      } else if (baseAlgorithm.equals("RC5")) {
         int blockBitLength = RIMFactoryUtilities.getBlockBitLength(algorithm, 64);
         return new RC5DecryptorEngine((RC5Key)key, blockBitLength >> 3);
      } else if (algorithm.equals("ECB") || algorithm.length() == 0) {
         return DecryptorFactory.getBlockDecryptorEngine(key, nextAlgorithm, null);
      } else if (algorithm.equals("RSA")) {
         return new RSADecryptorEngine((RSAPrivateKey)key);
      } else if (algorithm.equals("PKCS1")) {
         return new PKCS1UnformatterEngine((PrivateKeyDecryptorEngine)DecryptorFactory.getBlockDecryptorEngine(key, nextAlgorithm, null));
      } else {
         throw new IllegalArgumentException();
      }
   }
}
