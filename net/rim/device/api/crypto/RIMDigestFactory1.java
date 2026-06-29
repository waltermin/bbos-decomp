package net.rim.device.api.crypto;

final class RIMDigestFactory1 extends DigestFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"SHA224", "SHA384", "Null"};
   }

   @Override
   protected final Digest create(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.equals("SHA224")) {
         return new SHA224Digest();
      } else if (algorithm.equals("SHA384")) {
         return new SHA384Digest();
      } else if (algorithm.equals("Null")) {
         return new NullDigest();
      } else {
         throw new NoSuchAlgorithmException(algorithm);
      }
   }
}
