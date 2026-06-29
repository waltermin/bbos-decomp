package net.rim.device.api.crypto;

final class Crypto1AlgorithmInitialization implements Initialization {
   @Override
   public final void initialize() {
      SignatureSignerFactory.register(new RIMSignatureSignerFactory1());
      SymmetricKeyFactory.register(new RIMVariableLengthSymmetricKeyFactory1());
      SymmetricKeyFactory.register(new RIMSymmetricKeyFactory1());
      EncryptorFactory.register(new RIMCipherAlgorithmsEncryptorFactory1());
      EncryptorFactory.register(new RIMBlockEncryptorEngineFactory1());
      DecryptorFactory.register(new RIMCipherAlgorithmsDecryptorFactory1());
      DecryptorFactory.register(new RIMBlockDecryptorEngineFactory1());
      DigestFactory.register(new RIMDigestFactory1());
      MACFactory.register(new RIMMACFactory1());
      InitializationVectorFactory.register("DES", 64);
      InitializationVectorFactory.register("TripleDES", 64);
      InitializationVectorFactory.register("AES", 128);
      InitializationVectorFactory.register("RC5", 64);
   }
}
