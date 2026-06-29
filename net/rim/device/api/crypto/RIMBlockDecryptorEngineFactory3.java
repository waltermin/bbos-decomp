package net.rim.device.api.crypto;

import java.io.InputStream;

final class RIMBlockDecryptorEngineFactory3 extends DecryptorFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"RC2", "Skipjack", "CAST128", "OAEP", "ElGamal"};
   }

   @Override
   protected final Object create(String algorithm, String nextAlgorithm, Key key, InputStream stream, InitializationVector iv) {
      String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(algorithm);
      if (baseAlgorithm.equals("CAST128")) {
         return new CAST128DecryptorEngine((RC2CryptoToken)key);
      }

      if (baseAlgorithm.equals("Skipjack")) {
         return new SkipjackDecryptorEngine((SkipjackKey)key);
      }

      if (baseAlgorithm.equals("RC2")) {
         return new RC2DecryptorEngine((RC2Key)key);
      }

      if (algorithm.equals("OAEP")) {
         return new OAEPUnformatterEngine((PrivateKeyDecryptorEngine)DecryptorFactory.getBlockDecryptorEngine(key, nextAlgorithm, null));
      }

      if (algorithm.equals("ElGamal")) {
         if (stream == null) {
            throw new Object();
         } else {
            DHPrivateKey privateKey = (DHPrivateKey)key;
            int bitLength = stream.read() << 8;
            bitLength |= stream.read();
            if (bitLength < 0) {
               throw new Object();
            } else {
               int byteLength = bitLength + 7 >> 3;
               byte[] keyData = new byte[byteLength];
               if (byteLength != stream.read(keyData)) {
                  throw new Object();
               } else {
                  return new ElGamalDecryptorEngine(privateKey, (DHPublicKey)(new Object(privateKey.getDHCryptoSystem(), keyData)));
               }
            }
         }
      } else {
         throw new Object();
      }
   }
}
