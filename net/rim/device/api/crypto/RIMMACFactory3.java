package net.rim.device.api.crypto;

final class RIMMACFactory3 extends MACFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"CBCMAC"};
   }

   @Override
   protected final MAC create(String algorithm, String parameters, SymmetricKey key) {
      if (key == null) {
         throw new Object();
      }

      if (algorithm.equals("CBCMAC")) {
         if (parameters == null) {
            parameters = key.getAlgorithm();
         }

         BlockEncryptorEngine engine = EncryptorFactory.getBlockEncryptorEngine(key, parameters);
         return new SkipjackCryptoToken(engine);
      } else {
         throw new Object(algorithm);
      }
   }
}
