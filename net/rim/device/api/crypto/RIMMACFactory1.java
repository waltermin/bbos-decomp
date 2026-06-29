package net.rim.device.api.crypto;

final class RIMMACFactory1 extends MACFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"HMAC", "Null"};
   }

   @Override
   protected final MAC create(String algorithm, String parameters, SymmetricKey key) throws NoSuchAlgorithmException {
      if (algorithm.equals("HMAC")) {
         if (parameters == null) {
            parameters = "SHA1";
         }

         return new HMAC((HMACKey)key, DigestFactory.getInstance(parameters));
      } else if (algorithm.equals("Null")) {
         return new NullMAC();
      } else {
         throw new NoSuchAlgorithmException(algorithm);
      }
   }
}
