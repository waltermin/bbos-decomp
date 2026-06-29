package net.rim.device.api.crypto;

import java.io.OutputStream;

final class RIMCipherAlgorithmsEncryptorFactory1 extends EncryptorFactory {
   private static String[] ALGORITHM_LIST = new String[]{"ARC4", "RC4", "PKCS5"};

   @Override
   protected final String[] getFactoryAlgorithms() {
      return ALGORITHM_LIST;
   }

   @Override
   protected final Object create(String algorithm, String nextAlgorithm, Key key, OutputStream stream, InitializationVector iv) {
      if (algorithm.equals("ARC4") || algorithm.equals("RC4")) {
         return new ARC4PseudoRandomSource((ARC4Key)key);
      } else if (algorithm.equals("PKCS5")) {
         return new PKCS5FormatterEngine(EncryptorFactory.getBlockEncryptorEngine(key, nextAlgorithm, iv));
      } else {
         throw new Object(algorithm);
      }
   }
}
