package net.rim.device.api.crypto;

final class RIMDigestFactoryCrypto10 extends DigestFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"SHA256", "SHA1", "SHA512", "MD5"};
   }

   @Override
   protected final Digest create(String algorithm) {
      if (algorithm.equals("SHA1")) {
         return new SHA1Digest();
      } else if (algorithm.equals("SHA256")) {
         return new SHA256Digest();
      } else if (algorithm.equals("SHA512")) {
         return new SHA512Digest();
      } else if (algorithm.equals("MD5")) {
         return new MD5Digest();
      } else {
         throw new NoSuchAlgorithmException(algorithm);
      }
   }
}
