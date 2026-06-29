package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Initialization;

public final class Crypto1EncoderInitialization implements Initialization {
   @Override
   public final void initialize() {
      try {
         PrivateKeyDecoder.register(new KeyStore_RIM_PrivateKeyDecoder1(), new KeyStore_PrivateKeyDecoder());
         PrivateKeyEncoder.register(new KeyStore_RIM_PrivateKeyEncoder1());
         SymmetricKeyDecoder.register(new KeyStore_RIM_SymmetricKeyDecoder1(), new KeyStore_SymmetricKeyDecoder());
         SymmetricKeyEncoder.register(new KeyStore_RIM_SymmetricKeyEncoder1());
         PublicKeyDecoder.register(new WTLS_RIM_PublicKeyDecoder(), new WTLS_PublicKeyDecoder());
         PublicKeyEncoder.register(new WTLS_RIM_PublicKeyEncoder());
         SignatureDecoder.register(new WTLS_RIM_SignatureDecoder(), new WTLS_SignatureDecoder());
      } finally {
         throw new Object();
      }
   }
}
