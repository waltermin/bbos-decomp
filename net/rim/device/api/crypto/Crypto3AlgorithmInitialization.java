package net.rim.device.api.crypto;

final class Crypto3AlgorithmInitialization implements Initialization {
   @Override
   public final void initialize() {
      SymmetricKeyFactory.register(new RIMVariableLengthSymmetricKeyFactory3());
      SymmetricKeyFactory.register(new RIMSymmetricKeyFactory3());
      EncryptorFactory.register(new RIMBlockEncryptorEngineFactory3());
      EncryptorFactory.register(new RIMCipherAlgorithmsEncryptorFactory3());
      DecryptorFactory.register(new RIMCipherAlgorithmsDecryptorFactory3());
      DecryptorFactory.register(new RIMBlockDecryptorEngineFactory3());
      DigestFactory.register(new RIMDigestFactory3());
      MACFactory.register(new RIMMACFactory3());
      SignatureSignerFactory.register(new RIMSignatureSignerFactory3());
      InitializationVectorFactory.register("Skipjack", 64);
      InitializationVectorFactory.register("CAST128", 64);
      InitializationVectorFactory.register("RC2", 64);
   }
}
