package net.rim.device.api.crypto;

public final class ECDHKeyAgreement {
   private ECDHKeyAgreement() {
   }

   public static final byte[] generateSharedSecret(ECPrivateKey localPrivateKey, ECPublicKey remotePublicKey, boolean useCofactor) {
      if (localPrivateKey != null && remotePublicKey != null) {
         ECCryptoSystem cryptoSystem = localPrivateKey.getECCryptoSystem();
         if (!cryptoSystem.equals(remotePublicKey.getECCryptoSystem())) {
            throw new Object();
         } else {
            return localPrivateKey.getECCryptoToken()
               .generateECDHSharedSecret(
                  cryptoSystem.getCryptoTokenData(), localPrivateKey.getCryptoTokenData(), remotePublicKey.getPublicKeyData(), useCofactor
               );
         }
      } else {
         throw new Object();
      }
   }
}
