package net.rim.device.api.crypto;

import java.io.OutputStream;

final class RIMBlockEncryptorEngineFactory3 extends EncryptorFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"RC2", "Skipjack", "CAST128", "OAEP", "ElGamal"};
   }

   @Override
   protected final Object create(String algorithm, String nextAlgorithm, Key key, OutputStream stream, InitializationVector iv) {
      String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(algorithm);
      if (baseAlgorithm.equals("CAST128")) {
         return new CAST128EncryptorEngine((RC2CryptoToken)key);
      }

      if (baseAlgorithm.equals("Skipjack")) {
         return new SkipjackEncryptorEngine((SkipjackKey)key);
      }

      if (baseAlgorithm.equals("RC2")) {
         return new RC2EncryptorEngine((RC2Key)key);
      }

      if (algorithm.equals("OAEP")) {
         return new OAEPFormatterEngine((PublicKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null));
      }

      if (algorithm.equals("ElGamal")) {
         if (stream == null) {
            throw new Object();
         }

         DHPublicKey publicKey = (DHPublicKey)key;
         DHKeyPair keyPair = (DHKeyPair)(new Object(publicKey.getDHCryptoSystem()));
         byte[] keyData = keyPair.getDHPublicKey().getPublicKeyData();
         int bitLength = CryptoByteArrayArithmetic.getNumBits(keyData);
         stream.write(bitLength >> 8);
         stream.write(bitLength);
         int byteLength = bitLength + 7 >>> 3;
         stream.write(keyData, keyData.length - byteLength, byteLength);
         return new ElGamalEncryptorEngine(publicKey, keyPair);
      } else {
         throw new Object();
      }
   }
}
