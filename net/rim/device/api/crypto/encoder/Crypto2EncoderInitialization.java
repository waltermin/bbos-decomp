package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Initialization;

public final class Crypto2EncoderInitialization implements Initialization {
   @Override
   public final void initialize() {
      try {
         PrivateKeyDecoder.register(new MSCAPI_RIM_PrivateKeyDecoder(), new MSCAPI_PrivateKeyDecoder());
         PrivateKeyEncoder.register(new MSCAPI_RIM_PrivateKeyEncoder());
         PublicKeyDecoder.register(new MSCAPI_RIM_PublicKeyDecoder(), new MSCAPI_PublicKeyDecoder());
         PublicKeyEncoder.register(new MSCAPI_RIM_PublicKeyEncoder());
         PrivateKeyDecoder.register(new PKCS8_RIM_PrivateKeyDecoder2(), new PKCS8_PrivateKeyDecoder());
         PrivateKeyEncoder.register(new PKCS8_RIM_PrivateKeyEncoder2());
         SymmetricKeyDecoder.register(new PKCS8_RIM_SymmetricKeyDecoder2(), new PKCS8_SymmetricKeyDecoder());
         SymmetricKeyEncoder.register(new PKCS8_RIM_SymmetricKeyEncoder2());
         PublicKeyDecoder.register(new X509_RIM_PublicKeyDecoder2(), new X509_PublicKeyDecoder());
         PublicKeyEncoder.register(new X509_RIM_PublicKeyEncoder2());
         SignatureDecoder.register(new X509_RIM_SignatureDecoder2(), new X509_SignatureDecoder());
         SignatureEncoder.register(new X509_RIM_SignatureEncoder2());
      } finally {
         throw new RuntimeException();
      }
   }
}
