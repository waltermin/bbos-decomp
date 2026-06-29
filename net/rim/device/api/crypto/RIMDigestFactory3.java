package net.rim.device.api.crypto;

final class RIMDigestFactory3 extends DigestFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"MD4", "RIPEMD128", "RIPEMD160"};
   }

   @Override
   protected final Digest create(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.equals("MD4")) {
         return new MD4Digest();
      } else if (algorithm.equals("RIPEMD128")) {
         return new RIPEMD128Digest();
      } else if (algorithm.equals("RIPEMD160")) {
         return new RIPEMD160Digest();
      } else {
         throw new NoSuchAlgorithmException(algorithm);
      }
   }
}
