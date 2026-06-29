package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Initialization;

public final class Crypto3EncoderInitialization implements Initialization {
   @Override
   public final void initialize() {
      try {
         PrivateKeyDecoder.register(new KeyStore_RIM_PrivateKeyDecoder3(), new KeyStore_PrivateKeyDecoder());
         PrivateKeyEncoder.register(new KeyStore_RIM_PrivateKeyEncoder3());
         SymmetricKeyDecoder.register(new KeyStore_RIM_SymmetricKeyDecoder3(), new KeyStore_SymmetricKeyDecoder());
         SymmetricKeyEncoder.register(new KeyStore_RIM_SymmetricKeyEncoder3());
         PrivateKeyDecoder.register(new PKCS8_RIM_PrivateKeyDecoder3(), new PKCS8_PrivateKeyDecoder());
         PrivateKeyEncoder.register(new PKCS8_RIM_PrivateKeyEncoder3());
         SymmetricKeyDecoder.register(new PKCS8_RIM_SymmetricKeyDecoder3(), new PKCS8_SymmetricKeyDecoder());
         SymmetricKeyEncoder.register(new PKCS8_RIM_SymmetricKeyEncoder3());
         PublicKeyDecoder.register(new X509_RIM_PublicKeyDecoder3(), new X509_PublicKeyDecoder());
         PublicKeyEncoder.register(new X509_RIM_PublicKeyEncoder3());
         SignatureDecoder.register(new X509_RIM_SignatureDecoder3(), new X509_SignatureDecoder());
         SignatureEncoder.register(new X509_RIM_SignatureEncoder3());
      } finally {
         throw new RuntimeException();
      }
   }
}
