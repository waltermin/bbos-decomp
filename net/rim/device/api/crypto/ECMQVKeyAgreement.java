package net.rim.device.api.crypto;

public final class ECMQVKeyAgreement {
   private ECMQVKeyAgreement() {
   }

   public static final byte[] generateSharedSecret(
      ECPrivateKey localStaticPrivateKey,
      ECKeyPair localEphemeralKeyPair,
      ECPublicKey remoteStaticPublicKey,
      ECPublicKey remoteEphemeralPublicKey,
      boolean useCofactor
   ) {
      if (localStaticPrivateKey != null && localEphemeralKeyPair != null && remoteStaticPublicKey != null && remoteEphemeralPublicKey != null) {
         ECCryptoSystem cryptoSystem = localStaticPrivateKey.getECCryptoSystem();
         if (cryptoSystem.equals(remoteStaticPublicKey.getECCryptoSystem())
            && cryptoSystem.equals(remoteEphemeralPublicKey.getECCryptoSystem())
            && cryptoSystem.equals(localEphemeralKeyPair.getECCryptoSystem())) {
            ECPrivateKey localEphemeralPrivateKey = localEphemeralKeyPair.getECPrivateKey();
            ECPublicKey localEphemeralPublicKey = localEphemeralKeyPair.getECPublicKey();
            return localStaticPrivateKey.getECCryptoToken()
               .generateECMQVSharedSecret(
                  cryptoSystem.getCryptoTokenData(),
                  localStaticPrivateKey.getCryptoTokenData(),
                  localEphemeralPrivateKey.getCryptoTokenData(),
                  localEphemeralPublicKey.getCryptoTokenData(),
                  remoteStaticPublicKey.getPublicKeyData(),
                  remoteEphemeralPublicKey.getPublicKeyData(),
                  useCofactor
               );
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }
}
