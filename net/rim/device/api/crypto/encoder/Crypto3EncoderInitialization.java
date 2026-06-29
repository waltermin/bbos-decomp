package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Initialization;

public final class Crypto3EncoderInitialization implements Initialization {
   @Override
   public final void initialize() {
      try {
         PrivateKeyDecoder.register(new KeyStore_RIM_PrivateKeyDecoder3(), (PrivateKeyDecoder)(new Object()));
         PrivateKeyEncoder.register(new KeyStore_RIM_PrivateKeyEncoder3());
         SymmetricKeyDecoder.register(new KeyStore_RIM_SymmetricKeyDecoder3(), (SymmetricKeyDecoder)(new Object()));
         SymmetricKeyEncoder.register(new KeyStore_RIM_SymmetricKeyEncoder3());
         PrivateKeyDecoder.register(new PKCS8_RIM_PrivateKeyDecoder3(), (PrivateKeyDecoder)(new Object()));
         PrivateKeyEncoder.register(new PKCS8_RIM_PrivateKeyEncoder3());
         SymmetricKeyDecoder.register(new PKCS8_RIM_SymmetricKeyDecoder3(), (SymmetricKeyDecoder)(new Object()));
         SymmetricKeyEncoder.register(new PKCS8_RIM_SymmetricKeyEncoder3());
         PublicKeyDecoder.register(new X509_RIM_PublicKeyDecoder3(), (PublicKeyDecoder)(new Object()));
         PublicKeyEncoder.register(new X509_RIM_PublicKeyEncoder3());
         SignatureDecoder.register(new X509_RIM_SignatureDecoder3(), (SignatureDecoder)(new Object()));
         SignatureEncoder.register(new X509_RIM_SignatureEncoder3());
      } finally {
         throw new Object();
      }
   }
}
