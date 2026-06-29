package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareTripleDESCryptoToken extends TripleDESCryptoToken implements Persistable {
   private static SoftwareTripleDESCryptoToken _instance = new SoftwareTripleDESCryptoToken();
   private static final long ID_TEST_TRIPLEDES;

   static final SoftwareTripleDESCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareTripleDESCryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey() {
      return new SoftwareTripleDESCryptoToken$TripleDESKeyData();
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset) {
      return new SoftwareTripleDESCryptoToken$TripleDESKeyData(data, offset);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareTripleDESCryptoToken$TripleDESKeyData)) {
         throw new Object();
      }

      SoftwareTripleDESCryptoToken$TripleDESKeyData key = (SoftwareTripleDESCryptoToken$TripleDESKeyData)data;
      return new SoftwareTripleDESCryptoToken$TripleDESCipherContext(NativeBlockCipher.initializeDES(key.getData(), true));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareTripleDESCryptoToken$TripleDESCipherContext)) {
         throw new Object();
      }

      ((SoftwareTripleDESCryptoToken$TripleDESCipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareTripleDESCryptoToken$TripleDESKeyData)) {
         throw new Object();
      }

      SoftwareTripleDESCryptoToken$TripleDESKeyData key = (SoftwareTripleDESCryptoToken$TripleDESKeyData)data;
      return new SoftwareTripleDESCryptoToken$TripleDESCipherContext(NativeBlockCipher.initializeDES(key.getData(), false));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareTripleDESCryptoToken$TripleDESCipherContext)) {
         throw new Object();
      }

      ((SoftwareTripleDESCryptoToken$TripleDESCipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareTripleDESCryptoToken$TripleDESKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareTripleDESCryptoToken$TripleDESKeyData)data).getData();
      }
   }

   @Override
   public final int hashCode() {
      return 1315736989;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareTripleDESCryptoToken;
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{-43, 115, 76, 112, -12, 56, 33, 73};
      byte[] TEST_KEY = new byte[]{49, 112, 93, 104, 124, 1, -85, 25, 67, -123, 127, -85, -99, 70, 74, 38, -108, -101, 64, -70, -56, 97, 117, 67};
      int length = SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT.length;
      byte[] target = new byte[length];

      label61:
      try {
         TripleDESKey key = new TripleDESKey(TEST_KEY);
         TripleDESDecryptorEngine engine = new TripleDESDecryptorEngine(key);
         engine.decrypt(CIPHER_TEXT, 0, target, 0);
      } finally {
         break label61;
      }

      if (!Arrays.equals(target, 0, SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, length)) {
         throw new Object();
      }

      target = new byte[length];

      label55:
      try {
         TripleDESKey key = new TripleDESKey(TEST_KEY);
         TripleDESEncryptorEngine engine = new TripleDESEncryptorEngine(key);
         engine.encrypt(SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, target, 0);
      } finally {
         break label55;
      }

      if (!Arrays.equals(target, 0, CIPHER_TEXT, 0, length)) {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(-2106424517409732943L) == null) {
         selfTest();
         appRegistry.put(-2106424517409732943L, appRegistry);
      }
   }
}
