package net.rim.device.api.crypto;

final class RIMSymmetricKeyFactory3 extends SymmetricKeyFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"Skipjack", "CAST128"};
   }

   @Override
   protected final SymmetricKey create(String algorithm, byte[] data, int offset, int bitLength) {
      if (algorithm.equals("Skipjack") && bitLength >= 80) {
         return new SkipjackKey(data, offset);
      } else if (algorithm.equals("CAST128") && bitLength >= 128) {
         return new RC2CryptoToken(data, offset);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   protected final int getDefaultKeyLength(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.equals("Skipjack")) {
         return 80;
      } else if (algorithm.equals("CAST128")) {
         return 128;
      } else {
         throw new NoSuchAlgorithmException();
      }
   }
}
