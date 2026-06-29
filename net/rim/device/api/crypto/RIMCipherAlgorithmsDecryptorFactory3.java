package net.rim.device.api.crypto;

import java.io.InputStream;

final class RIMCipherAlgorithmsDecryptorFactory3 extends DecryptorFactory {
   private static String[] ALGORITHM_LIST = new String[]{"CFB", "CFB8", "OFB", "CTR", "ECIES", "EC"};

   @Override
   protected final String[] getFactoryAlgorithms() {
      return ALGORITHM_LIST;
   }

   @Override
   protected final Object create(String algorithm, String nextAlgorithm, Key key, InputStream stream, InitializationVector iv) throws NoSuchAlgorithmException {
      if (algorithm.equals("CFB")) {
         if (iv == null) {
            throw new IllegalArgumentException();
         } else {
            return new CFBDecryptor((SymmetricKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null), iv, stream, false);
         }
      } else if (algorithm.equals("CFB8")) {
         if (iv == null) {
            throw new IllegalArgumentException();
         } else {
            return new CFBDecryptor((SymmetricKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null), iv, stream, true);
         }
      } else if (algorithm.equals("OFB")) {
         if (iv == null) {
            throw new IllegalArgumentException();
         } else {
            return new OFBPseudoRandomSource((SymmetricKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null), iv);
         }
      } else if (algorithm.equals("CTR")) {
         if (iv == null) {
            throw new IllegalArgumentException();
         } else {
            return new CTRPseudoRandomSource((SymmetricKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, null), iv);
         }
      } else if (!algorithm.equals("ECIES") && !algorithm.equals("EC")) {
         throw new NoSuchAlgorithmException(algorithm);
      } else {
         return nextAlgorithm == null
            ? new ECIESDecryptor(stream, (ECPrivateKey)key)
            : new ECIESDecryptor(stream, (ECPrivateKey)key, nextAlgorithm, -1, null, -1, null, null, true);
      }
   }
}
