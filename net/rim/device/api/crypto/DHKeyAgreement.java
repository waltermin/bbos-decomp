package net.rim.device.api.crypto;

public final class DHKeyAgreement {
   private DHKeyAgreement() {
   }

   public static final byte[] generateSharedSecret(DHPrivateKey localPrivateKey, DHPublicKey remotePublicKey, boolean useCofactor) {
      if (localPrivateKey != null && remotePublicKey != null) {
         DHCryptoSystem cryptoSystem = localPrivateKey.getDHCryptoSystem();
         if (!cryptoSystem.equals(remotePublicKey.getDHCryptoSystem())) {
            throw new InvalidCryptoSystemException();
         } else {
            return localPrivateKey.getDHCryptoToken()
               .generateDHSharedSecret(cryptoSystem.getCryptoTokenData(), localPrivateKey.getCryptoTokenData(), remotePublicKey.getPublicKeyData(), useCofactor);
         }
      } else {
         throw new Object();
      }
   }
}
