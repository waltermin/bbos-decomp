package net.rim.device.api.crypto;

final class Crypto2AlgorithmInitialization implements Initialization {
   @Override
   public final void initialize() {
      DigestFactory.register(new RIMDigestFactory2());
   }
}
