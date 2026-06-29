package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareSkipjackCryptoToken extends SkipjackCryptoToken implements Persistable {
   private static SoftwareSkipjackCryptoToken _instance = new SoftwareSkipjackCryptoToken();
   private static final long ID_TEST_SKIPJACK;

   static final SoftwareSkipjackCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareSkipjackCryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey() {
      return new SoftwareSkipjackCryptoToken$SkipjackKeyData();
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset) {
      return new SoftwareSkipjackCryptoToken$SkipjackKeyData(data, offset);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareSkipjackCryptoToken$SkipjackKeyData)) {
         throw new Object();
      }

      SoftwareSkipjackCryptoToken$SkipjackKeyData key = (SoftwareSkipjackCryptoToken$SkipjackKeyData)data;
      return new SoftwareSkipjackCryptoToken$SkipjackCipherContext(NativeBlockCipher.initializeSkipjack(key.getData(), true));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareSkipjackCryptoToken$SkipjackCipherContext)) {
         throw new Object();
      }

      ((SoftwareSkipjackCryptoToken$SkipjackCipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareSkipjackCryptoToken$SkipjackKeyData)) {
         throw new Object();
      }

      SoftwareSkipjackCryptoToken$SkipjackKeyData key = (SoftwareSkipjackCryptoToken$SkipjackKeyData)data;
      return new SoftwareSkipjackCryptoToken$SkipjackCipherContext(NativeBlockCipher.initializeSkipjack(key.getData(), false));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareSkipjackCryptoToken$SkipjackCipherContext)) {
         throw new Object();
      }

      ((SoftwareSkipjackCryptoToken$SkipjackCipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareSkipjackCryptoToken$SkipjackKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareSkipjackCryptoToken$SkipjackKeyData)data).getData();
      }
   }

   @Override
   public final int hashCode() {
      return -730813910;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareSkipjackCryptoToken;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{-86, -69, -52, -35, 0, 17, 34, 51};
      byte[] CIPHER_TEXT = new byte[]{0, -45, 18, 122, -30, -54, -121, 37};
      byte[] TEST_KEY = new byte[]{17, 34, 51, 68, 85, 102, 119, -120, -103, 0};
      int length = PLAIN_TEXT.length;
      byte[] target = new byte[length];

      label61:
      try {
         SkipjackKey key = new SkipjackKey(TEST_KEY);
         SkipjackDecryptorEngine encryptorEngine = new SkipjackDecryptorEngine(key);
         encryptorEngine.decrypt(CIPHER_TEXT, 0, target, 0);
      } finally {
         break label61;
      }

      if (!Arrays.equals(target, 0, PLAIN_TEXT, 0, length)) {
         throw new Object();
      }

      target = new byte[length];

      label55:
      try {
         SkipjackKey key = new SkipjackKey(TEST_KEY);
         SkipjackEncryptorEngine encryptorEngine = new SkipjackEncryptorEngine(key);
         encryptorEngine.encrypt(PLAIN_TEXT, 0, target, 0);
      } finally {
         break label55;
      }

      if (!Arrays.equals(target, 0, CIPHER_TEXT, 0, length)) {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(-3707306364575989291L) == null) {
         selfTest();
         appRegistry.put(-3707306364575989291L, appRegistry);
      }
   }
}
