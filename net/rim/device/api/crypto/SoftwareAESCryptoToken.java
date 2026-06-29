package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareAESCryptoToken extends AESCryptoToken implements Persistable {
   private static SoftwareAESCryptoToken _instance = new SoftwareAESCryptoToken();
   private static final long ID_TEST_AES;

   static final SoftwareAESCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareAESCryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey(int bitLength) {
      return new SoftwareAESCryptoToken$AESKeyData(bitLength);
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset, int bitLength) {
      return new SoftwareAESCryptoToken$AESKeyData(data, offset, bitLength);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength) {
      if (!(data instanceof SoftwareAESCryptoToken$AESKeyData)) {
         throw new Object();
      }

      SoftwareAESCryptoToken$AESKeyData key = (SoftwareAESCryptoToken$AESKeyData)data;
      return new SoftwareAESCryptoToken$AESCipherContext(NativeBlockCipher.initializeAES(key.getData(), blockLength, true));
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength, boolean isInECMMode) {
      if (!(data instanceof SoftwareAESCryptoToken$AESKeyData)) {
         throw new Object();
      }

      SoftwareAESCryptoToken$AESKeyData key = (SoftwareAESCryptoToken$AESKeyData)data;
      return new SoftwareAESCryptoToken$AESCipherContext(NativeBlockCipher.initializeAES(key.getData(), blockLength, true, isInECMMode));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareAESCryptoToken$AESCipherContext)) {
         throw new Object();
      }

      ((SoftwareAESCryptoToken$AESCipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength) {
      if (!(data instanceof SoftwareAESCryptoToken$AESKeyData)) {
         throw new Object();
      }

      SoftwareAESCryptoToken$AESKeyData key = (SoftwareAESCryptoToken$AESKeyData)data;
      return new SoftwareAESCryptoToken$AESCipherContext(NativeBlockCipher.initializeAES(key.getData(), blockLength, false));
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength, boolean isInECMMode) {
      if (!(data instanceof SoftwareAESCryptoToken$AESKeyData)) {
         throw new Object();
      }

      SoftwareAESCryptoToken$AESKeyData key = (SoftwareAESCryptoToken$AESKeyData)data;
      return new SoftwareAESCryptoToken$AESCipherContext(NativeBlockCipher.initializeAES(key.getData(), blockLength, false, isInECMMode));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareAESCryptoToken$AESCipherContext)) {
         throw new Object();
      }

      ((SoftwareAESCryptoToken$AESCipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final int extractKeyDataLength(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareAESCryptoToken$AESKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareAESCryptoToken$AESKeyData)data).getLength();
      }
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareAESCryptoToken$AESKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareAESCryptoToken$AESKeyData)data).getData();
      }
   }

   @Override
   public final int hashCode() {
      return 1825876097;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareAESCryptoToken;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1};
      byte[] CIPHER_TEXT = new byte[]{105, -60, -32, -40, 106, 123, 4, 48, -40, -51, -73, -128, 112, -76, -59, 90};
      byte[] TEST_KEY = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
      int length = PLAIN_TEXT.length;
      byte[] targetNoECM = new byte[length];
      byte[] targetECM = new byte[length];

      label61:
      try {
         AESKey key1 = new AESKey(TEST_KEY);
         AESDecryptorEngine engine1 = new AESDecryptorEngine(key1, 16, false);
         engine1.decrypt(CIPHER_TEXT, 0, targetNoECM, 0);
         AESKey key2 = new AESKey(TEST_KEY);
         AESDecryptorEngine engine2 = new AESDecryptorEngine(key2, 16, true);
         engine2.decrypt(CIPHER_TEXT, 0, targetECM, 0);
      } finally {
         break label61;
      }

      if (!Arrays.equals(targetNoECM, 0, targetECM, 0, length)) {
         throw new Object();
      }

      targetNoECM = new byte[length];
      targetECM = new byte[length];

      label55:
      try {
         AESKey key1 = new AESKey(TEST_KEY);
         AESEncryptorEngine engine1 = new AESEncryptorEngine(key1, 16, false);
         engine1.encrypt(PLAIN_TEXT, 0, targetNoECM, 0);
         AESKey key2 = new AESKey(TEST_KEY);
         AESEncryptorEngine engine2 = new AESEncryptorEngine(key2, 16, true);
         engine2.encrypt(PLAIN_TEXT, 0, targetECM, 0);
      } finally {
         break label55;
      }

      if (!Arrays.equals(targetNoECM, 0, targetECM, 0, length)) {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(8205009952453931715L) == null) {
         selfTest();
         appRegistry.put(8205009952453931715L, appRegistry);
      }
   }
}
