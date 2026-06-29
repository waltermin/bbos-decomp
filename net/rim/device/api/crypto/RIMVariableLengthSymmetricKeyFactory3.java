package net.rim.device.api.crypto;

final class RIMVariableLengthSymmetricKeyFactory3 extends SymmetricKeyFactory {
   private static String[] ALGORITHM_LIST = new String[]{"RC2"};

   @Override
   protected final String[] getFactoryAlgorithms() {
      return ALGORITHM_LIST;
   }

   @Override
   protected final SymmetricKey create(String algorithm, byte[] data, int offset, int bitLength) {
      if (algorithm.equals("RC2")) {
         return new RC2Key(data, offset, Math.min(bitLength, 1024));
      } else {
         throw new Object(algorithm);
      }
   }

   @Override
   protected final int getDefaultKeyLength(String algorithm) {
      if (algorithm.equals("RC2")) {
         return 128;
      } else {
         throw new Object();
      }
   }
}
