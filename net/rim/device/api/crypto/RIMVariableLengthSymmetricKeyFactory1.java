package net.rim.device.api.crypto;

final class RIMVariableLengthSymmetricKeyFactory1 extends SymmetricKeyFactory {
   private static String[] ALGORITHM_LIST = new String[]{"AES", "ARC4", "RC4", "HMAC", "RC5"};

   @Override
   protected final String[] getFactoryAlgorithms() {
      return ALGORITHM_LIST;
   }

   @Override
   protected final SymmetricKey create(String algorithm, byte[] data, int offset, int bitLength) {
      if (algorithm.equals("AES")) {
         return new AESKey(data, offset, Math.min(bitLength, 256));
      } else if (algorithm.equals("ARC4") || algorithm.equals("RC4")) {
         return new ARC4Key(data, offset, Math.min(bitLength >> 3, 32));
      } else if (algorithm.equals("HMAC")) {
         return (SymmetricKey)(new Object(data, offset, Math.min(bitLength >> 3, 32)));
      } else if (algorithm.equals("RC5")) {
         return new RC5Key(data, offset, Math.min(bitLength, 2040));
      } else {
         throw new Object(algorithm);
      }
   }

   @Override
   protected final int getDefaultKeyLength(String algorithm) {
      if (algorithm.equals("AES")) {
         return 128;
      } else if (algorithm.equals("ARC4") || algorithm.equals("RC4")) {
         return 128;
      } else if (algorithm.equals("HMAC")) {
         return 128;
      } else if (algorithm.equals("RC5")) {
         return 128;
      } else {
         throw new Object();
      }
   }
}
