package net.rim.device.api.crypto;

final class RIMSymmetricKeyFactory1 extends SymmetricKeyFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"DES", "TripleDES"};
   }

   @Override
   protected final SymmetricKey create(String algorithm, byte[] data, int offset, int bitLength) {
      if (algorithm.equals("DES") && bitLength >= 64) {
         return new DESKey(data, offset);
      } else if (!algorithm.equals("TripleDES") || bitLength < 192 && bitLength != 128) {
         throw new IllegalArgumentException();
      } else {
         return new TripleDESKey(data, offset);
      }
   }

   @Override
   protected final int getDefaultKeyLength(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.equals("DES")) {
         return 64;
      } else if (algorithm.equals("TripleDES")) {
         return 192;
      } else {
         throw new NoSuchAlgorithmException();
      }
   }
}
