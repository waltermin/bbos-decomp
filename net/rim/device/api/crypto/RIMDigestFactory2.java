package net.rim.device.api.crypto;

final class RIMDigestFactory2 extends DigestFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"MD2"};
   }

   @Override
   protected final Digest create(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.equals("MD2")) {
         return new MD2Digest();
      } else {
         throw new NoSuchAlgorithmException(algorithm);
      }
   }
}
