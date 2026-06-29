package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class KEAKeyAgreement {
   private KEAKeyAgreement() {
   }

   public static final byte[] generateSharedSecret(
      KEAPrivateKey localStaticPrivateKey, KEAPrivateKey localEphemeralPrivateKey, KEAPublicKey remoteStaticPublicKey, KEAPublicKey remoteEphemeralPublicKey
   ) {
      if (localStaticPrivateKey != null && localEphemeralPrivateKey != null && remoteStaticPublicKey != null && remoteEphemeralPublicKey != null) {
         KEACryptoSystem cryptoSystem = localStaticPrivateKey.getKEACryptoSystem();
         if (cryptoSystem.equals(remoteStaticPublicKey.getKEACryptoSystem())
            && cryptoSystem.equals(remoteEphemeralPublicKey.getKEACryptoSystem())
            && cryptoSystem.equals(localEphemeralPrivateKey.getKEACryptoSystem())) {
            return localStaticPrivateKey.getKEACryptoToken()
               .generateKEASharedSecret(
                  cryptoSystem.getCryptoTokenData(),
                  localStaticPrivateKey.getCryptoTokenData(),
                  localEphemeralPrivateKey.getCryptoTokenData(),
                  remoteStaticPublicKey.getPublicKeyData(),
                  remoteEphemeralPublicKey.getPublicKeyData()
               );
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   public static final void selfTest() {
      byte[] sharedSecret = null;

      try {
         KEACryptoSystem cryptoSystem = new KEACryptoSystem(SelfTestData_PK2.KEA_P, SelfTestData_PK2.KEA_Q, SelfTestData_PK2.KEA_G);
         KEAPrivateKey staticPrivateKeyA = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_STATIC_PRIVATE_KEY_A));
         KEAPrivateKey ephemeralPrivateKeyA = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_EPHEMERAL_PRIVATE_KEY_A));
         KEAPrivateKey staticPrivateKeyB = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_STATIC_PRIVATE_KEY_B));
         KEAPublicKey staticPublicKeyB = new KEAPublicKey(cryptoSystem, staticPrivateKeyB.getPublicKeyData());
         KEAPrivateKey ephemeralPrivateKeyB = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_EPHEMERAL_PRIVATE_KEY_B));
         KEAPublicKey ephemeralPublicKeyB = new KEAPublicKey(cryptoSystem, ephemeralPrivateKeyB.getPublicKeyData());
         sharedSecret = generateSharedSecret(staticPrivateKeyA, ephemeralPrivateKeyA, staticPublicKeyB, ephemeralPublicKeyB);
         if (Arrays.equals(sharedSecret, SelfTestData_PK2.KEA_SHARED_SECRET_2_WAY)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_KEYAGREEMENT_KEA = -362746547921656193L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_KEYAGREEMENT_KEA) == null) {
         selfTest();
         appRegistry.put(ID_TEST_KEYAGREEMENT_KEA, appRegistry);
      }
   }
}
