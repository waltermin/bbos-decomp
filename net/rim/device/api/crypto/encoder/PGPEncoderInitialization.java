package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Initialization;

public final class PGPEncoderInitialization implements Initialization {
   @Override
   public final void initialize() {
      try {
         PrivateKeyEncoder.register(new KeyStore_RIM_PrivateKeyEncoderPGP());
         PrivateKeyDecoder.register(new KeyStore_RIM_PrivateKeyDecoderPGP(), (PrivateKeyDecoder)(new Object()));
      } finally {
         throw new Object();
      }
   }
}
